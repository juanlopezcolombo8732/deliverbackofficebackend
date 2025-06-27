package ar.com.deliverar.deliver.service;

import ar.com.deliverar.deliver.model.DetalleComision;
import ar.com.deliverar.deliver.model.SueldoEntity;
import ar.com.deliverar.deliver.model.Usuario;
import ar.com.deliverar.deliver.repository.DetalleComisionRepository;
import ar.com.deliverar.deliver.repository.SueldoRepository;
import ar.com.deliverar.deliver.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

@Service
public class SueldoService {

    @Autowired
    private DetalleComisionRepository detalleRepo;

    @Autowired
    private RestTemplate restTemplate;            // ← inyectamos RestTemplate

    // URL de tu broker/Core
    private final String PAYMENT_URL = "https://hub.deliver.ar/hub/publish";
    private static final String BROKER_URL = "https://hub.deliver.ar/hub/publish";

    @Autowired
    private SueldoRepository sueldoRepo;

    @Autowired
    private UsuarioRepository usuarioRepo;

    private static final String PAYMENT_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6ImJhY2tvZmZpY2Utc2VydmljZSIsImNhbGxiYWNrVVJMIjoiaHR0cHM6Ly9iY2tvZmZpY2UubGF0L21lc3NhZ2UiLCJyb2xlcyI6WyJ1c2VyIl0sImlhdCI6MTc1MTA0ODY3OCwiZXhwIjoxNzU4ODI0Njc4fQ.zGwD_yP2kv1gt5GQlzMSEMziKT_F6wk6KkMHp6Aw9JE";


    /**
     * Liquida comisiones de un repartidor en el período indicado:
     * - Suma montos de DetalleComision no liquidados
     * - Crea y guarda SueldoEntity con lista de detalles
     * - Marca cada detalle como liquidado
     */
    @Transactional
    public SueldoEntity liquidarPeriodo(
            Long usuarioId,
            LocalDate inicio,
            LocalDate fin
    ) {
        // 1) Buscamos al usuario real
        Usuario u = usuarioRepo.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + usuarioId));

        // 2) Convertir rangos de fecha a Instant
        Instant i0 = inicio.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant i1 = fin.plusDays(1)
                .atStartOfDay()
                .minusNanos(1)
                .toInstant(ZoneOffset.UTC);

        // 3) Recuperar detalles no liquidados
        List<DetalleComision> detalles = detalleRepo
                .findByUsuarioIdAndFechaBetweenAndLiquidadoFalse(usuarioId, i0, i1);

        // 4) Sumar montos
        double totalComision = detalles.stream()
                .mapToDouble(DetalleComision::getMonto)
                .sum();

        // 5) Crear SueldoEntity usando datos del usuario real
        SueldoEntity sueldo = new SueldoEntity();
        sueldo.setUsuario(u);
        sueldo.setPeriodoInicio(inicio);
        sueldo.setPeriodoFin(fin);
        sueldo.setSalarioBase(u.getSalarioBase());       // ahora tiene el valor correcto
        sueldo.setTotalComision(totalComision);
        sueldo.setTotalPagar(u.getSalarioBase() + totalComision);
        sueldo.setMoneda("ARS");
        sueldo.setDetalles(detalles);

        // 6) Guardar en BD
        SueldoEntity guardado = sueldoRepo.save(sueldo);

        // 7) Marcar detalles como liquidados
        detalles.forEach(d -> {
            d.setLiquidado(true);
            detalleRepo.save(d);
        });

        Map<String,Object> traceData = Map.of(
                "originModule", "deliver-backoffice",
                "traceId",       guardado.getId().toString()
        );
        Map<String,Object> data = Map.of(
                "traceData", traceData,
                "fromEmail", "owner@gmail.com",
                "toEmail",   guardado.getUsuario().getEmail(),
                "amount",    guardado.getTotalPagar(),
                "concept",   "Pago de Sueldo"
        );
        Map<String,Object> payload = Map.of(
                "topic", "fiat.payment.request",
                "data",  data
        );
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
// agrega el Bearer token
        headers.setBearerAuth(PAYMENT_TOKEN);

// Creamos la entidad con payload + headers
        HttpEntity<Map<String,Object>> request = new HttpEntity<>(payload, headers);

// Y enviamos
        ResponseEntity responseEntity =  restTemplate.postForEntity(PAYMENT_URL, request, Void.class);
        responseEntity.getBody();
        return guardado;

        //  Notificar al Core vía REST POST
        // Map<String,Object> payload = Map.of(
        //     "usuarioId", guardado.getUsuario().getId(),
        //     "montoPagar", guardado.getTotalPagar(),
        //     "moneda", guardado.getMoneda(),
        //     "periodoInicio", guardado.getPeriodoInicio().toString(),
        //     "periodoFin", guardado.getPeriodoFin().toString()
        // );
        // restTemplate.postForEntity(coreCallbackUrl, payload, Void.class);
    }

    @SuppressWarnings("unchecked")
    @Transactional
    public void procesarPaymentResponse(Map<String,Object> message) {
        Map<String,Object> data = (Map<String,Object>) message.get("data");
        if (!"SUCCESS".equals(data.get("status"))) {
            return; // si no anduvo, no seguimos
        }

        // 1) sacamos el traceId
        Map<String,Object> trace = (Map<String,Object>) data.get("traceData");
        Long sueldoId = Long.parseLong(trace.get("traceId").toString());

        // 2) traemos el entity completo
        SueldoEntity sueldo = findById(sueldoId);

        // 3) armamos el payload de salida
        Map<String,String> periodo = Map.of(
                "inicio", sueldo.getPeriodoInicio().toString(),
                "fin",    sueldo.getPeriodoFin().toString()
        );

        var detalles = sueldo.getDetalles().stream()
                .map(d -> Map.<String,Object>of(
                        "pedidoId", d.getPedidoId(),
                        "comision", d.getMonto()
                ))
                .toList();

        Map<String,Object> pagoPayload = Map.of(
                "periodo",        periodo,
                "repartidorId",   sueldo.getUsuario().getExternalId(),
                "nombre",         sueldo.getUsuario().getNombre() + " " + sueldo.getUsuario().getApellido(),
                "salarioBase",    sueldo.getSalarioBase(),
                "totalComision",  sueldo.getTotalComision(),
                "totalPagar",     sueldo.getTotalPagar(),
                "detalles",       detalles
        );

        Map<String,Object> outbound = Map.of(
                "module",  "Backoffice",
                "topic",   "sueldo.pago",
                "payload", pagoPayload
        );

        // 4) publicamos
        ResponseEntity en= restTemplate.postForEntity(BROKER_URL, outbound, Void.class);
        System.out.println(en.getBody());
        System.out.println(en.getStatusCode());


    }

    public SueldoEntity findById(Long id) {
        return sueldoRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Sueldo no encontrado: " + id));
    }

}
