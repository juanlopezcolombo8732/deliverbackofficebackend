package ar.com.deliverar.deliver.model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Entity
@Table(name = "pedidos")
public class Pedido {
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Proveedor tenant;
    @Id
    private String pedidoId;            // ID proveniente de Core/Client
    private String repartidorId;
    @Enumerated(EnumType.STRING)
    private EstadoPedido estado;
    private Double subtotal=10000.0;
    private String moneda;
    private Instant createdAt;


    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    private List<DetallePedido> detalles;
}