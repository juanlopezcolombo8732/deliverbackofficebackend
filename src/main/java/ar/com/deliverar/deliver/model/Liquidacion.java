package ar.com.deliverar.deliver.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import lombok.Data;

@Data
@Entity
@Table(name = "liquidaciones")
public class Liquidacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Usuario usuario;
    @Enumerated(EnumType.STRING)
    private EstadoLiquidacion estado = EstadoLiquidacion.PENDIENTE;

    private LocalDate fechaLiquidacion;
    private Double montoTotalLiquidado;
    private Double comisionCalculada;
}

