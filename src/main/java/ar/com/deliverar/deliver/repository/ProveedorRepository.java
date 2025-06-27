package ar.com.deliverar.deliver.repository;

import ar.com.deliverar.deliver.model.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {

        Optional<Proveedor> findByExternalId(String externalId);
}
