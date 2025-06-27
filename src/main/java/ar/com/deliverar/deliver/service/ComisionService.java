package ar.com.deliverar.deliver.service;

import ar.com.deliverar.deliver.model.ComisionEntity;
import ar.com.deliverar.deliver.model.DetalleComision;
import ar.com.deliverar.deliver.model.Pedido;
import ar.com.deliverar.deliver.model.Usuario;
import ar.com.deliverar.deliver.repository.ComisionRepository;
import ar.com.deliverar.deliver.repository.DetalleComisionRepository;
import ar.com.deliverar.deliver.repository.PedidoRepository;
import ar.com.deliverar.deliver.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Transactional
public class ComisionService {

    private final PedidoRepository pedidoRepo;
    private final ComisionRepository comisionRepo;
    private final UsuarioRepository usuarioRepo;
    private final DetalleComisionRepository detalleRepo;

    @Autowired
    public ComisionService(PedidoRepository pedidoRepo,
                           ComisionRepository comisionRepo,
                           UsuarioRepository usuarioRepo,
                           DetalleComisionRepository detalleRepo) {
        this.pedidoRepo    = pedidoRepo;
        this.comisionRepo = comisionRepo;
        this.usuarioRepo  = usuarioRepo;
        this.detalleRepo  = detalleRepo;
    }

    /**
     * Procesa cuando un pedido se marca ENTREGADO:
     * 1) Comisión de plataforma (tenant)
     * 2) Comisión de repartidor (según % en Usuario)
     */
    public void procesarPedidoEntregado(String pedidoId) {
        // 1) Obtener pedido
        Pedido pedido = pedidoRepo.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado: " + pedidoId));
        double subtotal = pedido.getSubtotal();

        // --------------------------
        // 2) COMISIÓN PLATFORM / TENANT
        // --------------------------
        double comisionPlataforma = subtotal * 0.20;
        double montoTenant = subtotal - comisionPlataforma;

        ComisionEntity comTenant = new ComisionEntity();
        comTenant.setPedidoId(pedidoId);
        comTenant.setTenant(pedido.getTenant());
        comTenant.setSubtotal(subtotal);
        comTenant.setComisionPlataforma(comisionPlataforma);
        comTenant.setMontoTransferir(montoTenant);
        comTenant.setMoneda(pedido.getMoneda());
        comTenant.setTimestamp(Instant.now());
        comisionRepo.save(comTenant);

        // --------------------------
        // 3) COMISIÓN REPARTIDOR
        // --------------------------
        String repartidorId = pedido.getRepartidorId();
        Usuario rep = usuarioRepo.findByExternalId(repartidorId)
                .orElseThrow(() -> new RuntimeException("Repartidor no encontrado: " + repartidorId));
        double porcentaje = rep.getPorcentajeComision() != null
                ? rep.getPorcentajeComision()
                : 0.0;
        double montoRepartidor = subtotal * porcentaje;

        DetalleComision detalle = new DetalleComision();
        detalle.setPedidoId(pedidoId);
        detalle.setUsuario(rep);
        detalle.setMonto(montoRepartidor);
        detalle.setFecha(Instant.now());
        // liquidado queda false por defecto
        detalleRepo.save(detalle);

        // 4) Actualizar totales en Usuario
        rep.setTotalVentas(rep.getTotalVentas() + subtotal);
        rep.setSaldoActual(rep.getSaldoActual() + montoRepartidor);
        usuarioRepo.save(rep);
    }
}
