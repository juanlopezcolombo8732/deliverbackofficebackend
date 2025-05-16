package ar.com.deliverar.deliver.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "configuracion")
public class Configuracion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Ej: "comision_por_entrega", "salario_base_estandar", etc.
    private String clave;

    private String valor;
}

