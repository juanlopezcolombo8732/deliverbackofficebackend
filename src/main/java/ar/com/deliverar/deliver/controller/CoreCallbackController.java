package ar.com.deliverar.deliver.controller;

import ar.com.deliverar.deliver.model.Pedido;
import ar.com.deliverar.deliver.model.Proveedor;
import ar.com.deliverar.deliver.model.Usuario;
import ar.com.deliverar.deliver.repository.UsuarioRepository;
import ar.com.deliverar.deliver.service.ComisionService;
import ar.com.deliverar.deliver.service.PedidoService;
import ar.com.deliverar.deliver.service.ProveedorService;
import ar.com.deliverar.deliver.service.UsuarioService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.HEAD, RequestMethod.POST})
@RestController
@RequestMapping("/message")
public class CoreCallbackController {

    private final ProveedorService proveedorService;
    private final ComisionService comisionService;
    private final PedidoService pedidoService;


    private final UsuarioService usuarioService;

    @Autowired
    public CoreCallbackController(ProveedorService proveedorService,
                                  ComisionService comisionService,PedidoService pedidoService,UsuarioRepository usuarioRepository, UsuarioService usuarioService, ObjectMapper mapper) {
        this.proveedorService = proveedorService;
        this.comisionService  = comisionService;
        this.usuarioService = usuarioService;
        this.pedidoService = pedidoService ;

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
            case "tenant.creadoTest":
                proveedorService.upsertFromPayload(payload);
                break;

            case "pedido.entregado":
                String pedidoId = pedidoService.upsertAndReturnId(payload);
                comisionService.procesarPedidoEntregado(pedidoId);
                break;

            case "delivery.nuevoRepartidor":
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
}
