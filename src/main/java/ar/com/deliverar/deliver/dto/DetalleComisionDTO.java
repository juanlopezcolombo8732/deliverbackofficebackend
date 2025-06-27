// src/main/java/ar/com/deliverar/deliver/dto/DetalleComisionDTO.java
package ar.com.deliverar.deliver.dto;

import java.time.Instant;
import lombok.Data;

@Data
public class DetalleComisionDTO {
    private String pedidoId;
    private Double monto;
    private Instant fecha;
    private boolean liquidado;

    public DetalleComisionDTO(String pedidoId, Double monto, Instant fecha, boolean liquidado) {
        this.pedidoId = pedidoId;
        this.monto = monto;
        this.fecha = fecha;
        this.liquidado = liquidado;
    }
    // getters y setters...
}
