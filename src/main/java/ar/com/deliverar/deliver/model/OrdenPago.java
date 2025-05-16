package ar.com.deliverar.deliver.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "ordenes_pago")
public class OrdenPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fechaEmision;

    private Double montoTotal;

    private String estado; // Ej: "Pendiente", "Pagado"

    @ManyToOne
    @JoinColumn(name = "repartidor_id")
    private Usuario repartidor;
    @OneToMany(mappedBy = "ordenPago", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Comision> comisiones;



}
