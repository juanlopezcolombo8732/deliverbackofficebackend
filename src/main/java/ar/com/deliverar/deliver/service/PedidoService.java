package ar.com.deliverar.deliver.service;

import ar.com.deliverar.deliver.model.Pedido;
import ar.com.deliverar.deliver.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PedidoService {

    private final PedidoRepository pedidoRepository;

    @Autowired
    public PedidoService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    /** Crea o actualiza un pedido */
    public Pedido create(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    /** Lista todos los pedidos */
    public List<Pedido> findAll() {
        return pedidoRepository.findAll();
    }

    /** Busca un pedido por su ID */
    public Optional<Pedido> findById(String pedidoId) {
        return pedidoRepository.findById(pedidoId);
    }

    /** Elimina un pedido por ID */
    public void delete(String pedidoId) {
        pedidoRepository.deleteById(pedidoId);
    }
}
