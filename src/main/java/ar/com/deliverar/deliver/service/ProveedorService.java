package ar.com.deliverar.deliver.service;

import ar.com.deliverar.deliver.model.Proveedor;
import ar.com.deliverar.deliver.repository.ProveedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
}
