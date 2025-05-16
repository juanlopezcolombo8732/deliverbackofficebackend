package ar.com.deliverar.deliver.controller;

import ar.com.deliverar.deliver.model.Factura;
import ar.com.deliverar.deliver.model.Factura.EstadoFactura;
import ar.com.deliverar.deliver.model.Proveedor;
import ar.com.deliverar.deliver.service.FacturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/facturas")
@CrossOrigin(origins = "*")
public class FacturaController {

    @Autowired
    private FacturaService facturaService;

    // Emitir nueva factura
    @PostMapping("/emitir")
    public ResponseEntity<Factura> emitirFactura(@RequestParam Long proveedorId,
                                                 @RequestParam String numero,
                                                 @RequestParam Double montoTotal) {
        try {
            Factura factura = facturaService.emitirFactura(proveedorId, numero, montoTotal);
            return ResponseEntity.ok(factura);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping
    public ResponseEntity<Factura> crearFactura(@RequestBody Factura factura) {
        try {
            Factura creada = facturaService.crearFacturaDesdeJson(factura);
            return ResponseEntity.ok(creada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }


    // Cambiar estado
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Factura> cambiarEstado(@PathVariable Long id,
                                                 @RequestParam EstadoFactura estado) {
        try {
            Factura actualizada = facturaService.actualizarEstado(id, estado);
            return ResponseEntity.ok(actualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Obtener todas
    @GetMapping
    public List<Factura> obtenerTodas() {
        return facturaService.obtenerTodas();
    }

    // Obtener por ID
    @GetMapping("/{id}")
    public ResponseEntity<Factura> obtenerPorId(@PathVariable Long id) {
        Optional<Factura> factura = facturaService.obtenerPorId(id);
        return factura.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Obtener por proveedor
    @GetMapping("/proveedor/{proveedorId}")
    public List<Factura> obtenerPorProveedor(@PathVariable Long proveedorId) {
        return facturaService.obtenerPorProveedor(proveedorId);
    }


}
