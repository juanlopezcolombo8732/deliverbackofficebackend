package ar.com.deliverar.deliver.controller;

import ar.com.deliverar.deliver.model.ComisionEntity;
import ar.com.deliverar.deliver.model.Factura;
import ar.com.deliverar.deliver.model.Factura.EstadoFactura;
import ar.com.deliverar.deliver.model.SueldoEntity;
import ar.com.deliverar.deliver.repository.ComisionRepository;
import ar.com.deliverar.deliver.repository.FacturaRepository;
import ar.com.deliverar.deliver.repository.SueldoRepository;
import ar.com.deliverar.deliver.service.FacturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/** Controlador de Comisiones */
@RestController
@RequestMapping("/api/comisiones")
public class ComisionController {

    @Autowired
    private ComisionRepository comisionRepository;

    /** Todas las comisiones */
    @GetMapping
    public List<ComisionEntity> listar() {
        return comisionRepository.findAll();
    }

    /** Comisión por ID */
    @GetMapping("/{id}")
    public ResponseEntity<ComisionEntity> obtenerPorId(@PathVariable Long id) {
        return comisionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** Comisiones en un rango de tiempo (ISO date-time) */
    @GetMapping("/periodo")
    public List<ComisionEntity> porPeriodo(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant start,
            @RequestParam("end")   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end
    ) {
        return comisionRepository.findByTimestampBetween(start, end);
    }
}