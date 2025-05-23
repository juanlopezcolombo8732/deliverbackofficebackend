package ar.com.deliverar.deliver.dto;

import lombok.Data;

import java.time.LocalDate;


@Data
public class RegistrarFacturaDTO {
    private String numero;
    private String descripcion;
    private Double montoTotal;
    private String referenciaPago;
    private LocalDate fechaEmision;
    private Long proveedorId;
    private String tipoComprobante;
    private String condicionIva;
    private String cuitEmisor;
    private String cuitReceptor;
    private String domicilioFiscal;
    private String notas;
    // condicionPago se setea por defecto como "Contado"
}
