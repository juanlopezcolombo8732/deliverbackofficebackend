package ar.com.deliverar.deliver.repository;

import ar.com.deliverar.deliver.model.Comercio;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ComercioRepository extends JpaRepository<Comercio, String> {

}
