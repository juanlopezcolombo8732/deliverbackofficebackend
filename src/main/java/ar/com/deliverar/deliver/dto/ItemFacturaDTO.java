package ar.com.deliverar.deliver.dto;

import lombok.Data;



@Data
public class ItemFacturaDTO {
    private String descripcion;
    private int cantidad = 1;
    private double precioUnitario;
    private double porcentajeIVA = 21.0; // Por defecto
}
