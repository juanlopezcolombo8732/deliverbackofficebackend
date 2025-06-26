package ar.com.deliverar.deliver.controller;

import ar.com.deliverar.deliver.model.SueldoEntity;
import ar.com.deliverar.deliver.repository.SueldoRepository;
import ar.com.deliverar.deliver.service.SueldoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/sueldos")
public class SueldoController {

    @Autowired
    private SueldoRepository sueldoRepository;

    @Autowired
    private SueldoService sueldoService;

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

    @PostMapping("/liquidar")
    public ResponseEntity<SueldoEntity> liquidar(
            @RequestParam("usuarioId") Long usuarioId,
            @RequestParam("inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam("fin")    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin
    ) {
        SueldoEntity resultado = sueldoService.liquidarPeriodo(usuarioId, inicio, fin);
        return ResponseEntity.ok(resultado);
    }

    /** Crear un sueldo manualmente */
    @PostMapping ()
    public SueldoEntity crear(@RequestBody SueldoEntity s) {
        return sueldoRepository.save(s);
    }
}

