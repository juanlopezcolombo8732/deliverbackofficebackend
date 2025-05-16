package ar.com.deliverar.deliver.service;

import ar.com.deliverar.deliver.model.Factura;
import ar.com.deliverar.deliver.model.Proveedor;
import ar.com.deliverar.deliver.model.Factura.EstadoFactura;
import ar.com.deliverar.deliver.repository.FacturaRepository;
import ar.com.deliverar.deliver.repository.ProveedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class FacturaService {

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private ProveedorRepository proveedorRepository;

    // Crear una nueva factura para un proveedor específico
    public Factura emitirFactura(Long proveedorId, String numero, Double montoTotal) {
        Proveedor proveedor = proveedorRepository.findById(proveedorId)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado con ID: " + proveedorId));

        Factura factura = new Factura();
        factura.setProveedor(proveedor);
        factura.setNumero(numero);
        factura.setMontoTotal(montoTotal);
        factura.setFechaEmision(LocalDate.now());
        factura.setEstado(EstadoFactura.EMITIDA);

        return facturaRepository.save(factura);
    }

    // Cambiar el estado de una factura
    public Factura actualizarEstado(Long idFactura, EstadoFactura nuevoEstado) {
        Factura factura = facturaRepository.findById(idFactura)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada con ID: " + idFactura));

        factura.setEstado(nuevoEstado);
        return facturaRepository.save(factura);
    }

    // Obtener todas las facturas
    public List<Factura> obtenerTodas() {
        return facturaRepository.findAll();
    }

    // Obtener una factura por ID
    public Optional<Factura> obtenerPorId(Long id) {
        return facturaRepository.findById(id);
    }

    public Factura crearFacturaDesdeJson(Factura factura) {
        Long proveedorId = factura.getProveedor().getId();

        Proveedor proveedor = proveedorRepository.findById(proveedorId)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado con ID: " + proveedorId));

        factura.setProveedor(proveedor);
        factura.setFechaEmision(LocalDate.now());
        factura.setEstado(EstadoFactura.EMITIDA);

        return facturaRepository.save(factura);
    }

    // Obtener facturas por proveedor
    public List<Factura> obtenerPorProveedor(Long proveedorId) {
        return facturaRepository.findByProveedorId(proveedorId);
    }

    public Factura guardarFactura(Factura factura) {
        return facturaRepository.save(factura);
    }


    public Factura marcarComoPagada(Long facturaId) {
        Factura factura = facturaRepository.findById(facturaId)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada con ID: " + facturaId));

        factura.setEstado(Factura.EstadoFactura.PAGADA);
        return facturaRepository.save(factura);
    }
}
