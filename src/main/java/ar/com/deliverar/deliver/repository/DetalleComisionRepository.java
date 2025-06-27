package ar.com.deliverar.deliver.repository;

import ar.com.deliverar.deliver.model.DetalleComision;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.time.Instant;
import java.util.List;

public interface DetalleComisionRepository extends JpaRepository<DetalleComision, Long> {

    /**
     * Trae todas las comisiones de un usuario, en un rango de fechas,
     * que aún NO hayan sido liquidadas.
     */
    List<DetalleComision> findByUsuarioIdAndFechaBetweenAndLiquidadoFalse(
            Long usuarioId,
            Instant fechaInicio,
            Instant fechaFin
    );

    List<DetalleComision> findByUsuarioId(Long usuarioId);

}
