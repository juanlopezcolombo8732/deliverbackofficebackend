package ar.com.deliverar.deliver.model;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Data
@Entity
@Table(name = "comisiones")
public class ComisionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String pedidoId;

    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Proveedor tenant;           // Relación al tenant

    private Double subtotal;
    private Double comisionPlataforma;
    private Double montoTransferir;
    private String moneda;
    private Instant timestamp;
}
