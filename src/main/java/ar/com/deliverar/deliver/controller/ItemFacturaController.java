package ar.com.deliverar.deliver.controller;

import ar.com.deliverar.deliver.dto.ItemFacturaDTO;
import ar.com.deliverar.deliver.model.ItemFactura;
import ar.com.deliverar.deliver.service.ItemFacturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/facturas/{facturaId}/items")
@CrossOrigin(origins = "*")
public class ItemFacturaController {

    @Autowired
    private ItemFacturaService itemFacturaService;

    // Crear un nuevo ítem para una factura
    @PostMapping
    public ResponseEntity<ItemFactura> crearItem(@PathVariable Long facturaId,
                                                 @RequestBody ItemFacturaDTO dto) {
        try {
            ItemFactura nuevoItem = itemFacturaService.agregarItem(facturaId, dto);
            return ResponseEntity.ok(nuevoItem);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Actualizar un ítem existente
    @PutMapping("/{itemId}")
    public ResponseEntity<ItemFactura> actualizarItem(@PathVariable Long facturaId,
                                                      @PathVariable Long itemId,
                                                      @RequestBody ItemFacturaDTO dto) {
        try {
            ItemFactura actualizado = itemFacturaService.actualizarItem(facturaId, itemId, dto);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Obtener todos los ítems de una factura
    @GetMapping
    public ResponseEntity<List<ItemFactura>> obtenerItems(@PathVariable Long facturaId) {
        List<ItemFactura> items = itemFacturaService.obtenerItemsPorFactura(facturaId);
        return ResponseEntity.ok(items);
    }
}
