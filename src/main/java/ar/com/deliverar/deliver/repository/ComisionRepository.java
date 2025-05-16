package ar.com.deliverar.deliver.repository;

import ar.com.deliverar.deliver.model.Comision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComisionRepository extends JpaRepository<Comision, Long> {

    // Buscar todas las comisiones asociadas a un repartidor por su ID
    List<Comision> findByRepartidorId(Long repartidorId);

    List<Comision> findByRepartidorIdAndEstado(Long repartidorId, String estado);
    


    // Buscar comisiones que todavía no fueron asociadas a una orden de pago
    List<Comision> findByOrdenPagoIsNull();

    // Buscar comisiones no asociadas y de un repartidor específico
    List<Comision> findByOrdenPagoIsNullAndRepartidorId(Long repartidorId);

    List<Comision> findByRepartidorIdAndOrdenPagoIsNull(Long repartidorId);

}
