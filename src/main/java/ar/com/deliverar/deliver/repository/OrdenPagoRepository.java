package ar.com.deliverar.deliver.repository;

import ar.com.deliverar.deliver.model.OrdenPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdenPagoRepository extends JpaRepository<OrdenPago, Long> {

    // Buscar todas las órdenes de pago de un repartidor
    List<OrdenPago> findByRepartidorId(Long repartidorId);
}
