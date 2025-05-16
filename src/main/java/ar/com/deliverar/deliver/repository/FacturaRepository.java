package ar.com.deliverar.deliver.repository;

import ar.com.deliverar.deliver.model.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {
    List<Factura> findByProveedorId(Long proveedorId);
    List<Factura> findByEstado(Factura.EstadoFactura estado);
}
