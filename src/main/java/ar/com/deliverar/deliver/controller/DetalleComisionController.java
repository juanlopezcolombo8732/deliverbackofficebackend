package ar.com.deliverar.deliver.controller;

import ar.com.deliverar.deliver.dto.DetalleComisionDTO;
import ar.com.deliverar.deliver.model.DetalleComision;
import ar.com.deliverar.deliver.service.DetalleComisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios/{usuarioId}/comisiones")
public class DetalleComisionController {

    private final DetalleComisionService detalleService;

    @Autowired
    public DetalleComisionController(DetalleComisionService detalleService) {
        this.detalleService = detalleService;
    }

    /**
     * GET /api/usuarios/{usuarioId}/comisiones
     * Devuelve la lista de DetalleComision de ese usuario.
     */
    @GetMapping
    public ResponseEntity<List<DetalleComisionDTO>> listarPorUsuario(
            @PathVariable Long usuarioId
    ) {
        List<DetalleComisionDTO> detalles = detalleService.getDetallesPorUsuario(usuarioId);
        if (detalles.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(detalles);
    }
}
