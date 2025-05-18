package ar.com.deliverar.deliver.controller;
import ar.com.deliverar.deliver.model.EstadoLiquidacion;

import ar.com.deliverar.deliver.model.Liquidacion;
import ar.com.deliverar.deliver.service.LiquidacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/liquidaciones")
@CrossOrigin(origins = "*")
public class LiquidacionController {

    @Autowired
    private LiquidacionService liquidacionService;

    @PostMapping("/generar")
    public ResponseEntity<List<Liquidacion>> generarLiquidaciones() {
        List<Liquidacion> liquidaciones = liquidacionService.generarLiquidaciones();
        return ResponseEntity.ok(liquidaciones);
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Liquidacion> actualizarEstado(@PathVariable Long id, @RequestParam EstadoLiquidacion estado) {
        Optional<Liquidacion> liquidacionOpt = liquidacionService.actualizarEstado(id, estado);
        return liquidacionOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Liquidacion>> obtenerTodas() {
        List<Liquidacion> liquidaciones = liquidacionService.obtenerTodas();
        return ResponseEntity.ok(liquidaciones);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Liquidacion>> historialPorUsuario(@PathVariable Long usuarioId) {
        List<Liquidacion> historial = liquidacionService.obtenerHistorialPorUsuario(usuarioId);
        return ResponseEntity.ok(historial);
    }
}
