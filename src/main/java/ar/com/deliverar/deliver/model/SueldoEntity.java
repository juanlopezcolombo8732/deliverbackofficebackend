package ar.com.deliverar.deliver.model;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Table(name = "sueldos")
public class SueldoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    private LocalDate periodoInicio;
    private LocalDate periodoFin;
    private Double salarioBase;
    private Double totalComision;
    private Double totalPagar;
    private String moneda;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "sueldo_id")
    private List<DetalleComision> detalles;
}

