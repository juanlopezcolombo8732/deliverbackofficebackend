package ar.com.deliverar.deliver.repository;

import ar.com.deliverar.deliver.model.ComisionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface ComisionRepository extends JpaRepository<ComisionEntity, Long> {
    /**
     * Busca todas las comisiones cuyo timestamp esté entre start (inclusive) y end (exclusive).
     */
    List<ComisionEntity> findByTimestampBetween(Instant start, Instant end);
}
