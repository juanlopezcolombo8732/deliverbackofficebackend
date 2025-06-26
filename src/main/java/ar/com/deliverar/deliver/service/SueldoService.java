package ar.com.deliverar.deliver.service;

import ar.com.deliverar.deliver.model.DetalleComision;
import ar.com.deliverar.deliver.model.SueldoEntity;
import ar.com.deliverar.deliver.model.Usuario;
import ar.com.deliverar.deliver.repository.DetalleComisionRepository;
import ar.com.deliverar.deliver.repository.SueldoRepository;
import ar.com.deliverar.deliver.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class SueldoService {

    @Autowired
    private DetalleComisionRepository detalleRepo;

    @Autowired
    private SueldoRepository sueldoRepo;

    @Autowired
    private UsuarioRepository usuarioRepo;

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

}
