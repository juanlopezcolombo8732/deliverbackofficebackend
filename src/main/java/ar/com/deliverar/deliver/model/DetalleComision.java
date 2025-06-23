package ar.com.deliverar.deliver.model;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "detalle_comision")
public class DetalleComision {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String pedidoId;
    private Double comision;
}
