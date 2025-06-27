package ar.com.deliverar.deliver.model;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double totalVentas = 0.0;     // Total de ventas realizadas
    private Double saldoActual = 0.0;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String direccion;
    private String ciudad;
    private String pais;


    @Column(name = "external_id", unique = true)
    private String externalId;

    private String rol;
    private String departamento;

    private LocalDate fechaContratacion;
    private Double salarioBase=700000.0;
    private Double porcentajeComision=0.02;

    private String nombreContactoEmergencia;
    private String telefonoContactoEmergencia;



}
