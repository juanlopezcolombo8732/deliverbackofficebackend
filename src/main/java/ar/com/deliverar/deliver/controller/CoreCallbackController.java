package ar.com.deliverar.deliver.controller;

import ar.com.deliverar.deliver.model.Comercio;
import ar.com.deliverar.deliver.model.Pedido;
import ar.com.deliverar.deliver.model.Proveedor;
import ar.com.deliverar.deliver.model.Usuario;
import ar.com.deliverar.deliver.repository.UsuarioRepository;
import ar.com.deliverar.deliver.service.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Map;

@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.HEAD, RequestMethod.POST})
@RestController
@RequestMapping("/message")
public class CoreCallbackController {

    private final ProveedorService proveedorService;
    private final ComisionService comisionService;
    private final PedidoService pedidoService;
    private final ComercioService comercioService;
    private final SueldoService sueldoService;


    private final UsuarioService usuarioService;

    @Autowired
    public CoreCallbackController(ProveedorService proveedorService,ComercioService comercioService,RestTemplate restTemplate,
                                  ComisionService comisionService,PedidoService pedidoService,UsuarioRepository usuarioRepository, UsuarioService usuarioService, ObjectMapper mapper,SueldoService sueldoService) {
        this.proveedorService = proveedorService;
        this.comisionService  = comisionService;
        this.usuarioService = usuarioService;
        this.pedidoService = pedidoService ;
        this.comercioService = comercioService;
        this.sueldoService = sueldoService;



    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.HEAD})
    public ResponseEntity<String> healthCheck(
            @RequestParam(value = "secret", required = false) String secret,
            @RequestParam(value = "challenge", required = false) String challenge
    ) {
        // Si Core envía un challenge, devolverlo en texto plano
        if (challenge != null) {
            return ResponseEntity.ok()
                    .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
                    .body(challenge);
        }
        // Respuesta estándar para GET sin challenge
        return ResponseEntity.ok("");
    }

    @PostMapping
    public ResponseEntity<Void> onCoreEvent(
            @RequestHeader("x-topic") String eventType,
            @RequestBody Map<String,Object> payload
    ) {
        switch(eventType) {
            case "fiat.payment.response":
                System.out.println("ENTRO CONFIR PAGO CREADO");

                sueldoService.procesarPaymentResponse(payload);                break;

            case "pedido.creado":
                System.out.println("PEDIDO CREADO");

                handlePedidoCreado(payload);
                break;
            case "comercio.creado":
                System.out.println("COMERCIO CREADO");

                handleComercioCreado(payload);
                break;


            case "tenant.creado":
                System.out.println("TENANT CREADO");
                proveedorService.upsertFromPayload(payload);
                break;

            case "pedido.entregado":
                System.out.println("PEDIDO ENTREGADO");

                String pedidoId = pedidoService.upsertAndReturnId(payload);
                comisionService.procesarPedidoEntregado(pedidoId);
                break;


            case "delivery.nuevoRepartidor":
                System.out.println("DELIVERY CREADO");

                // Directamente pasamos el Map que nos llega
                handleNuevoRepartidor(payload);
                break;

            default:
                return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @SuppressWarnings("unchecked")
    private void handleNuevoRepartidor(Map<String,Object> p) {
        // Ahora 'p' es directamente el JSON que envías en el body
        String extId = p.get("repartidorId").toString();

        Usuario u = usuarioService.findOrCreateByExternalId(extId);

        u.setRol("REPARTIDOR");
        u.setNombre(   (String) p.get("nombre")   );
        u.setApellido( (String) p.get("apellido") );
        u.setEmail(    (String) p.get("email")    );
        u.setTelefono(p.get("telefono").toString());

        if (p.containsKey("salarioBase")) {
            u.setSalarioBase(((Number)p.get("salarioBase")).doubleValue());
        }
        if (p.containsKey("porcentajeComision")) {
            u.setPorcentajeComision(((Number)p.get("porcentajeComision")).doubleValue());
        }

        if (u.getTotalVentas() == null) {
            u.setTotalVentas(0.0);
        }
        if (u.getSaldoActual() == null) {
            u.setSaldoActual(0.0);
        }

        usuarioService.guardar(u);
    }

    @SuppressWarnings("unchecked")
    private void handleComercioCreado(Map<String,Object> body) {
        // support both direct payload and wrapped
        Map<String,Object> evt = body;
        if (body.containsKey("payload")) {
            evt = (Map<String,Object>) body.get("payload");
        }
        Map<String,Object> comm;
        if (evt.containsKey("comercio")) {
            comm = (Map<String,Object>) evt.get("comercio");
        } else {
            comm = evt;
        }

        String comercioId = comm.get("comercio_id").toString();
        String tenantId   = comm.get("tenant_id").toString();
        String calle      = (String) comm.get("calle");
        String numero     = comm.get("numero").toString();

        comercioService.upsertFromEvent(comercioId, tenantId, calle, numero);
    }

    @SuppressWarnings("unchecked")
    private void handlePedidoCreado(Map<String,Object> body) {
        // 1) Parseo los campos básicos
        String pedidoId        = body.get("pedidoId").toString();
        String comercioId      = body.get("comercio_id").toString();


        // 2) Traigo el Comercio y a partir de él el Proveedor (tenant)
        Comercio comm = comercioService.findById(comercioId);
        Proveedor tenant = comm.getTenant();

        // 3) Creo la entidad Pedido y la completo
        Pedido p = new Pedido();
        p.setPedidoId(pedidoId);
        p.setTenant(tenant);

        // si tenés más campos (productos, fecha, etc.) los parseás aquí…

        // 4) Lo guardás con tu service/repo
        pedidoService.guardar(p);
    }


}
