package ar.com.deliverar.deliver.controller;

import ar.com.deliverar.deliver.dto.RegistrarFacturaDTO;
import ar.com.deliverar.deliver.model.Factura;
import ar.com.deliverar.deliver.model.Factura.EstadoFactura;
import ar.com.deliverar.deliver.service.FacturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/facturas")
@CrossOrigin(origins = "*")
public class FacturaController {

    @Autowired
    private FacturaService facturaService;


    /** Todas las facturas */
    @GetMapping
    public List<Factura> listar() {
        return facturaService.obtenerTodas();
    }

    /** Factura por ID */
    @GetMapping("/{id}")
    public ResponseEntity<Factura> obtenerPorId(@PathVariable Long id) {
        return facturaService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** Crear factura desde DTO */
    @PostMapping
    public Factura crear(@RequestBody RegistrarFacturaDTO dto) {
        return facturaService.crearFacturaDesdeDto(dto);
    }

    /** Cambiar estado */
    @PutMapping("/{id}/estado")
    public Factura actualizarEstado(
            @PathVariable Long id,
            @RequestParam EstadoFactura estado
    ) {
        return facturaService.actualizarEstado(id, estado);
    }

    /** Facturas de un proveedor */
    @GetMapping("/proveedor/{provId}")
    public List<Factura> porProveedor(@PathVariable Long provId) {
        return facturaService.obtenerPorProveedor(provId);
    }

    /** Descargar PDF inline */
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> descargarPdf(@PathVariable Long id) {
        byte[] pdf = facturaService.generarPdf(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
                ContentDisposition.builder("inline")
                        .filename("factura-" + id + ".pdf").build()
        );
        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }

    /** Generar facturas automáticas de comisiones por periodo */
    @PostMapping("/generar-periodo")
    public List<Factura> generarPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin
    ) {
        return facturaService.generarFacturasPorPeriodo(inicio, fin);
    }
}