package ar.com.deliverar.deliver.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "comercios")
public class Comercio {

    @Id
    private String id;

    private String calle;
    private String numero;


    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Proveedor tenant;
}
