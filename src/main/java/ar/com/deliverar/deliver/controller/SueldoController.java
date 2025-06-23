package ar.com.deliverar.deliver.controller;

import ar.com.deliverar.deliver.model.SueldoEntity;
import ar.com.deliverar.deliver.repository.SueldoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sueldos")
public class SueldoController {

    @Autowired
    private SueldoRepository sueldoRepository;

    /** Todos los sueldos */
    @GetMapping
    public List<SueldoEntity> listar() {
        return sueldoRepository.findAll();
    }

    /** Sueldo por ID */
    @GetMapping("/{id}")
    public ResponseEntity<SueldoEntity> obtenerPorId(@PathVariable Long id) {
        return sueldoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** Crear un sueldo manualmente */
    @PostMapping
    public SueldoEntity crear(@RequestBody SueldoEntity s) {
        return sueldoRepository.save(s);
    }
}