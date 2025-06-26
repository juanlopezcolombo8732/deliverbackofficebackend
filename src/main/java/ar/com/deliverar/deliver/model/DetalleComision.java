package ar.com.deliverar.deliver.model;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Data
@Entity
@Table(name = "detalle_comision")
public class DetalleComision {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String pedidoId;
    private Double monto;

    private Instant fecha;

    /**
     * true una vez que esa comisión ha sido incluida en una liquidación de sueldo
     */
    private boolean liquidado = false;
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
 }
