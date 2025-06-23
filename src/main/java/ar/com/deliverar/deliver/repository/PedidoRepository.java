package ar.com.deliverar.deliver.repository;

import ar.com.deliverar.deliver.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, String> {}

