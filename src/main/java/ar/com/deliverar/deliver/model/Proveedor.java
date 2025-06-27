package ar.com.deliverar.deliver.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "proveedores")
@JsonIgnoreProperties({"pedidos", "facturas"})
public class Proveedor {




    private String nombre;
    private String cuit;
    private String direccion;
    private String email;
    private String telefono;
    private String categoriaFiscal;
    @Column(name = "external_id",  unique = true)
    private String externalId;

    @JsonManagedReference
    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL)
    private List<Pedido> pedidos;

    @JsonManagedReference
    @OneToMany(mappedBy = "proveedor", cascade = CascadeType.ALL)
    private List<Factura> facturas;



    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
}


