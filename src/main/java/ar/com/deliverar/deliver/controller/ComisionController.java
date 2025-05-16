package ar.com.deliverar.deliver.controller;

import ar.com.deliverar.deliver.model.Comision;
import ar.com.deliverar.deliver.service.ComisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/comisiones")
@CrossOrigin(origins = "*")
public class ComisionController {

    @Autowired
    private ComisionService comisionService;

    @GetMapping
    public List<Comision> obtenerTodas() {
        return comisionService.obtenerTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comision> obtenerPorId(@PathVariable Long id) {
        Optional<Comision> comision = comisionService.obtenerPorId(id);
        return comision.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/repartidor/{idRepartidor}")
    public List<Comision> obtenerPorRepartidor(@PathVariable Long idRepartidor) {
        return comisionService.obtenerPorRepartidor(idRepartidor);
    }

    @PostMapping
    public Comision crear(@RequestBody Comision comision) {
        return comisionService.crearComision(comision);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        comisionService.eliminarComision(id);
        return ResponseEntity.noContent().build();
    }
}
