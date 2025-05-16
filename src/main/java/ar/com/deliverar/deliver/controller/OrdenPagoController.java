package ar.com.deliverar.deliver.controller;

import ar.com.deliverar.deliver.model.OrdenPago;
import ar.com.deliverar.deliver.service.OrdenPagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ordenes-pago")
@CrossOrigin(origins = "*")
public class OrdenPagoController {

    @Autowired
    private OrdenPagoService ordenPagoService;

    // Genera una nueva orden de pago para un repartidor con comisiones pendientes
    @PostMapping("/generar")
    public ResponseEntity<OrdenPago> generarOrdenPago(@RequestParam Long repartidorId) {
        try {
            OrdenPago orden = ordenPagoService.generarOrdenPagoParaRepartidor(repartidorId);
            return ResponseEntity.ok(orden);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Marca una orden de pago como pagada
    @PatchMapping("/{id}/marcar-pagada")
    public ResponseEntity<OrdenPago> marcarPagada(@PathVariable Long id) {
        try {
            OrdenPago orden = ordenPagoService.marcarComoPagada(id);
            return ResponseEntity.ok(orden);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Obtiene todas las órdenes de pago de un repartidor
    @GetMapping("/por-repartidor/{repartidorId}")
    public List<OrdenPago> obtenerPorRepartidor(@PathVariable Long repartidorId) {
        return ordenPagoService.obtenerOrdenesPorRepartidor(repartidorId);
    }

    // Obtiene una orden de pago por su ID
    @GetMapping("/{id}")
    public ResponseEntity<OrdenPago> obtenerPorId(@PathVariable Long id) {
        return ordenPagoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Lista todas las órdenes de pago del sistema
    @GetMapping
    public List<OrdenPago> obtenerTodas() {
        return ordenPagoService.obtenerTodas();
    }
}
