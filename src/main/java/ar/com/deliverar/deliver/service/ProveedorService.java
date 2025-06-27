package ar.com.deliverar.deliver.service;

import ar.com.deliverar.deliver.model.Proveedor;
import ar.com.deliverar.deliver.repository.ProveedorRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProveedorService {

    @Autowired
    private ProveedorRepository proveedorRepository;


    public Proveedor crearProveedor(Proveedor proveedor) {
        return proveedorRepository.save(proveedor);
    }

    public Optional<Proveedor> obtenerPorId(Long id) {
        return proveedorRepository.findById(id);
    }

    public List<Proveedor> obtenerTodos() {
        return proveedorRepository.findAll();
    }

    public void eliminarProveedor(Long id) {
        proveedorRepository.deleteById(id);
    }

    @Transactional
    public Proveedor upsertFromPayload(Map<String, Object> payload) {
        String extId = payload.get("tenant_id").toString();

        Proveedor entidad = proveedorRepository.findByExternalId(extId)
                .orElseGet(() -> {
                    Proveedor nuevo = new Proveedor();
                    nuevo.setExternalId(extId);
                    return nuevo;
                });

        // Actualizo campos
        entidad.setNombre((String) payload.get("nombre"));
        entidad.setCuit((String) payload.get("razon_social"));
        entidad.setDireccion((String) payload.get("sitio_web"));
        entidad.setEmail((String) payload.get("email"));
        entidad.setTelefono((String) payload.get("telefono"));

        return proveedorRepository.save(entidad);
    }

    }

