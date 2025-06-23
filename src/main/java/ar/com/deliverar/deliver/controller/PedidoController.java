package ar.com.deliverar.deliver.controller;


import ar.com.deliverar.deliver.model.Pedido;
import ar.com.deliverar.deliver.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    @Autowired
    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping
    public List<Pedido> listar() {
        return pedidoService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> obtenerPorId(@PathVariable String id) {
        return pedidoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Pedido> crear(@RequestBody Pedido pedido) {
        Pedido creado = pedidoService.create(pedido);
        return ResponseEntity.ok(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pedido> actualizar(
            @PathVariable String id,
            @RequestBody Pedido cambios
    ) {
        return pedidoService.findById(id)
                .map(existing -> {
                    existing.setEstado(cambios.getEstado());
                    existing.setSubtotal(cambios.getSubtotal());
                    existing.setMoneda(cambios.getMoneda());
                    existing.setRepartidorId(cambios.getRepartidorId());
                    existing.setCreatedAt(cambios.getCreatedAt());
                    existing.setTenant(cambios.getTenant());
                    existing.setDetalles(cambios.getDetalles());
                    return ResponseEntity.ok(pedidoService.create(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        if (pedidoService.findById(id).isPresent()) {
            pedidoService.delete(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
