package ar.com.deliverar.deliver.service;

import ar.com.deliverar.deliver.model.Pedido;
import ar.com.deliverar.deliver.repository.PedidoRepository;
import ar.com.deliverar.deliver.repository.ProveedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class PedidoService {

    private final PedidoRepository pedidoRepository;

    @Autowired
    public PedidoService(PedidoRepository pedidoRepository,ProveedorRepository proveedorRepository) {
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

    @Transactional
    public String upsertAndReturnId(Map<String,Object> payload) {
        Instant cita = Instant.parse("2025-06-27T19:00:00Z");
        String pedidoId = payload.get("pedidoId").toString();

        // 1) Buscar o instanciar
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseGet(() -> {
                    Pedido p = new Pedido();
                    p.setPedidoId(pedidoId);
                    return p;
                });

        // 2) Mapear campos del payload
        // Asumimos que el evento trae estos campos:
        pedido.setEstado(
                ar.com.deliverar.deliver.model.EstadoPedido.valueOf(
                        payload.getOrDefault("estado", "ENTREGADO").toString()
                )
        );
        pedido.setMoneda("ARS");
        pedido.setCreatedAt(cita);




        pedido.setRepartidorId(payload.get("repartidorId").toString());

        // 3) Asociar al Proveedor (tenant)



        // 4) Guardar
        pedidoRepository.save(pedido);

        return pedidoId;
    }

    public Pedido guardar(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }
}
