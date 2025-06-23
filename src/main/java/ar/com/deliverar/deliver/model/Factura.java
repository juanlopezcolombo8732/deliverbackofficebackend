// src/main/java/ar/com/deliverar/deliver/model/Factura.java
package ar.com.deliverar.deliver.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;


@Data
@Entity
@Table(name = "facturas")
public class Factura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Mantengo tu lógica existente:
    private String numero;
    private LocalDate fechaEmision;
    private Double montoTotal;
    private String tipoComprobante;
    private String condicionIva;
    private String cuitEmisor;
    private String cuitReceptor;
    private String domicilioFiscal;
    private String notas;
    private Double ivaTotal = 0.0;
    private String condicionPago = "Contado";
    private String descripcion;

    // Relación al tenant (Proveedor)
    @ManyToOne
    @JoinColumn(name = "proveedor_id")
    private Proveedor proveedor;

    @Enumerated(EnumType.STRING)
    private EstadoFactura estado;

    // Items de la factura
    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<ItemFactura> items;

    // Campos nuevos para liquidación de comisiones:
    private LocalDate periodoInicio;
    private LocalDate periodoFin;
    private Double totalComisiones;
    public enum EstadoFactura {
        PENDIENTE,
        EMITIDA,
        PAGADA
    }
}
