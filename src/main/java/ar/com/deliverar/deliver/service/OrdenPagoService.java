package ar.com.deliverar.deliver.service;

import ar.com.deliverar.deliver.model.Comision;
import ar.com.deliverar.deliver.model.OrdenPago;
import ar.com.deliverar.deliver.model.Usuario;
import ar.com.deliverar.deliver.repository.ComisionRepository;
import ar.com.deliverar.deliver.repository.OrdenPagoRepository;
import ar.com.deliverar.deliver.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class OrdenPagoService {

    @Autowired
    private OrdenPagoRepository ordenPagoRepository;

    @Autowired
    private ComisionRepository comisionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Crear nueva orden de pago
    public OrdenPago crearOrdenPago(Long idRepartidor, List<Long> idsComisiones, Double montoTotal) {
        Usuario repartidor = usuarioRepository.findById(idRepartidor)
                .orElseThrow(() -> new RuntimeException("Repartidor no encontrado con ID: " + idRepartidor));

        List<Comision> comisiones = comisionRepository.findAllById(idsComisiones);
        if (comisiones.isEmpty()) {
            throw new RuntimeException("No se encontraron comisiones con los IDs proporcionados");
        }

        OrdenPago orden = new OrdenPago();
        orden.setRepartidor(repartidor);
        orden.setComisiones(comisiones);
        orden.setMontoTotal(montoTotal);
        orden.setFechaEmision(LocalDate.now());
        orden.setEstado("PENDIENTE");

        return ordenPagoRepository.save(orden);
    }

    public OrdenPago generarOrdenPagoParaRepartidor(Long repartidorId) {
        Usuario repartidor = usuarioRepository.findById(repartidorId)
                .orElseThrow(() -> new RuntimeException("Repartidor no encontrado con ID: " + repartidorId));

        List<Comision> comisionesPendientes = comisionRepository.findByRepartidorIdAndOrdenPagoIsNull(repartidorId);
        if (comisionesPendientes.isEmpty()) {
            throw new RuntimeException("No hay comisiones pendientes para el repartidor con ID: " + repartidorId);
        }

        double montoTotal = comisionesPendientes.stream()
                .mapToDouble(Comision::getMonto)
                .sum();

        OrdenPago orden = new OrdenPago();
        orden.setRepartidor(repartidor);
        orden.setMontoTotal(montoTotal);
        orden.setFechaEmision(LocalDate.now());
        orden.setEstado("PENDIENTE");

        // Primero guardamos la orden de pago
        orden = ordenPagoRepository.save(orden);

        // Luego asociamos cada comisión a esta orden
        for (Comision comision : comisionesPendientes) {
            comision.setOrdenPago(orden);
        }

        // Y guardamos todas las comisiones actualizadas
        comisionRepository.saveAll(comisionesPendientes);

        return orden;
    }


    // Marcar orden como pagada
    public OrdenPago marcarComoPagada(Long idOrden) {
        OrdenPago orden = ordenPagoRepository.findById(idOrden)
                .orElseThrow(() -> new RuntimeException("Orden de pago no encontrada con ID: " + idOrden));

        orden.setEstado("PAGADA");
        return ordenPagoRepository.save(orden);
    }

    // Buscar por repartidor
    public List<OrdenPago> obtenerOrdenesPorRepartidor(Long idRepartidor) {
        return ordenPagoRepository.findByRepartidorId(idRepartidor);
    }

    // Obtener todas
    public List<OrdenPago> obtenerTodas() {
        return ordenPagoRepository.findAll();
    }

    // Obtener por ID
    public Optional<OrdenPago> obtenerPorId(Long id) {
        return ordenPagoRepository.findById(id);
    }
}
