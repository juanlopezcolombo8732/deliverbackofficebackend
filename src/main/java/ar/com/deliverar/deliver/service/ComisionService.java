package ar.com.deliverar.deliver.service;

import ar.com.deliverar.deliver.model.ComisionEntity;
import ar.com.deliverar.deliver.model.Proveedor;
import ar.com.deliverar.deliver.model.Pedido;
import ar.com.deliverar.deliver.repository.PedidoRepository;
import ar.com.deliverar.deliver.repository.ComisionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Transactional
public class ComisionService {

    private final PedidoRepository pedidoRepository;
    private final ComisionRepository comisionRepository;

    @Autowired
    public ComisionService(PedidoRepository pedidoRepository,
                           ComisionRepository comisionRepository) {
        this.pedidoRepository = pedidoRepository;
        this.comisionRepository = comisionRepository;
    }

    /**
     * Procesa la lógica cuando un pedido se marca como ENTREGADO.
     * Calcula la comisión de la plataforma (20%) y persiste una ComisionEntity.
     */
    public void procesarPedidoEntregado(String pedidoId) {
        // 1) Buscar el pedido y obtener el subtotal
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado: " + pedidoId));
        double subtotal = pedido.getSubtotal();

        // 2) Calcular comisión y monto a transferir
        double comisionPlataforma = subtotal * 0.20;
        double montoTransferir    = subtotal - comisionPlataforma;

        // 3) Crear y guardar la entidad ComisionEntity
        ComisionEntity comision = new ComisionEntity();
        comision.setPedidoId(pedidoId);
        Proveedor tenant = pedido.getTenant();
        comision.setTenant(tenant);
        comision.setSubtotal(subtotal);
        comision.setComisionPlataforma(comisionPlataforma);
        comision.setMontoTransferir(montoTransferir);
        comision.setMoneda("ARS");
        comision.setTimestamp(Instant.now());

        comisionRepository.save(comision);
    }
}

