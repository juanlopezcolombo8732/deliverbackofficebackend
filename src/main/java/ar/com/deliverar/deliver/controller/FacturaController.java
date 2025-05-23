package ar.com.deliverar.deliver.controller;

import ar.com.deliverar.deliver.dto.RegistrarFacturaDTO;
import ar.com.deliverar.deliver.model.Factura;
import ar.com.deliverar.deliver.model.Factura.EstadoFactura;
import ar.com.deliverar.deliver.service.FacturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

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
    // src/main/java/ar/com/deliverar/deliver/controller/FacturaController.java

    @PatchMapping("/{id}/pagar")
    public ResponseEntity<Factura> pagarFactura(@PathVariable Long id) {
        try {
            Factura pagada = facturaService.marcarComoPagada(id);
            return ResponseEntity.ok(pagada);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Factura> crearFactura(@RequestBody RegistrarFacturaDTO dto) {
        try {
            Factura factura = facturaService.crearFacturaDesdeDto(dto);
            return ResponseEntity.ok(factura);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
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

    // src/main/java/ar/com/deliverar/deliver/controller/FacturaController.java

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> descargarFacturaPdf(@PathVariable Long id) {
        byte[] pdfBytes = facturaService.generarPdf(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("factura_" + id + ".pdf")
                .build());
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }


}
