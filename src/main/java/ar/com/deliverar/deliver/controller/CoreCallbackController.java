package ar.com.deliverar.deliver.controller;

import ar.com.deliverar.deliver.model.Proveedor;
import ar.com.deliverar.deliver.model.Usuario;
import ar.com.deliverar.deliver.repository.UsuarioRepository;
import ar.com.deliverar.deliver.service.ComisionService;
import ar.com.deliverar.deliver.service.ProveedorService;
import ar.com.deliverar.deliver.service.UsuarioService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.HEAD, RequestMethod.POST})
@RestController
@RequestMapping("/message")
public class CoreCallbackController {

    private final ProveedorService proveedorService;
    private final ComisionService comisionService;

    private final UsuarioService usuarioService;

    @Autowired
    public CoreCallbackController(ProveedorService proveedorService,
                                  ComisionService comisionService,UsuarioRepository usuarioRepository, UsuarioService usuarioService, ObjectMapper mapper) {
        this.proveedorService = proveedorService;
        this.comisionService  = comisionService;
        this.usuarioService = usuarioService;

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
            @RequestHeader("X-Event-Type") String eventType,
            @RequestBody Map<String,Object> payload
    ) {
        switch(eventType) {
            case "tenant.created":
                handleTenantCreated(payload);
                break;

            case "pedido.entregado":
                handlePedidoEntregado(payload);
                break;

            case "delivery.nuevoRepartidor":
                handleNuevoRepartidor(payload);
                break;

            default:
                return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @SuppressWarnings("unchecked")
    private void handleNuevoRepartidor(Map<String,Object> body) {
        // extraigo el map interno "payload"
        Map<String,Object> p = (Map<String,Object>) body.get("payload");

        // mapeo campos del payload a mi entidad Usuario
        Usuario u = new Usuario();
        // asumimos que rol = "REPARTIDOR" por convención
        u.setRol("REPARTIDOR");
        u.setNombre(   (String) p.get("nombre")   );
        u.setApellido( (String) p.get("apellido") );
        u.setEmail(    (String) p.get("email")    );
        Object tel = p.get("telefono");
        u.setTelefono(tel != null ? tel.toString() : null);

        // inicializamos otros campos por defecto
        u.setTotalVentas(0.0);
        u.setSaldoActual(0.0);
        // (puedes completar domicilio, ciudad, etc. si vienen en el payload)

        // guardamos en BD
        usuarioService.crearUsuario(u);
    }

    private void handleTenantCreated(Map<String,Object> payload) {

        Proveedor nuevo = new Proveedor();
                    nuevo.setNombre((String) payload.get("nombre"));
                    nuevo.setCuit((String) payload.get("cuit"));
                    nuevo.setDireccion((String) payload.get("direccion"));
                    nuevo.setEmail((String) payload.get("email"));
                    nuevo.setTelefono((String) payload.get("telefono"));
                    nuevo.setCategoriaFiscal((String) payload.get("categoriaFiscal"));


        proveedorService.crearProveedor(nuevo);
    }

    private void handlePedidoEntregado(Map<String,Object> payload) {
        // Asumimos que tu ComisionService expone un método así:
        String pedidoId = payload.get("pedidoId").toString();
        comisionService.procesarPedidoEntregado(pedidoId);
    }
}
