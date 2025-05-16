package ar.com.deliverar.deliver.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "facturas")
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numero;

    private LocalDate fechaEmision;

    private Double montoTotal;

    @ManyToOne
    @JoinColumn(name = "proveedor_id")
    private Proveedor proveedor;

    @Enumerated(EnumType.STRING)
    private EstadoFactura estado;

    public enum EstadoFactura {
        PENDIENTE,
        EMITIDA,
        PAGADA
    }
}
