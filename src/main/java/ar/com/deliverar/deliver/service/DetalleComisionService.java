package ar.com.deliverar.deliver.service;

import ar.com.deliverar.deliver.dto.DetalleComisionDTO;
import ar.com.deliverar.deliver.model.DetalleComision;
import ar.com.deliverar.deliver.repository.DetalleComisionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DetalleComisionService {

    private final DetalleComisionRepository detalleRepo;

    @Autowired
    public DetalleComisionService(DetalleComisionRepository detalleRepo) {
        this.detalleRepo = detalleRepo;
    }

    /**
     * Devuelve todos los detalles de comisión de un usuario (liquidados o no).
     */


    public List<DetalleComisionDTO> getDetallesPorUsuario(Long usuarioId) {
        return detalleRepo.findByUsuarioId(usuarioId).stream()
                .map(d -> new DetalleComisionDTO(
                        d.getPedidoId(),
                        d.getMonto(),
                        d.getFecha(),
                        d.isLiquidado()))
                .toList();
    }
}
