package ar.com.deliverar.deliver.repository;

import ar.com.deliverar.deliver.model.Liquidacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LiquidacionRepository extends JpaRepository<Liquidacion, Long> {
    List<Liquidacion> findByUsuarioId(Long usuarioId);
}

