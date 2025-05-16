package ar.com.deliverar.deliver.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "comisiones")
public class Comision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fecha;
    private Double monto;

    private String estado; // Ej: "PENDIENTE", "PAGADA"

    @ManyToOne
    @JoinColumn(name = "repartidor_id")
    private Usuario repartidor;

    @ManyToOne
    @JoinColumn(name = "orden_pago_id")
    private OrdenPago ordenPago;

}

