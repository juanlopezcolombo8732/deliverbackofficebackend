package ar.com.deliverar.deliver.service;

import ar.com.deliverar.deliver.model.Comision;
import ar.com.deliverar.deliver.repository.ComisionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ComisionService {

    @Autowired
    private ComisionRepository comisionRepository;

    public List<Comision> obtenerTodas() {
        return comisionRepository.findAll();
    }

    public Optional<Comision> obtenerPorId(Long id) {
        return comisionRepository.findById(id);
    }

    public List<Comision> obtenerPorRepartidor(Long repartidorId) {
        return comisionRepository.findByRepartidorId(repartidorId);
    }

    public Comision crearComision(Comision comision) {
        return comisionRepository.save(comision);
    }

    public void eliminarComision(Long id) {
        comisionRepository.deleteById(id);
    }
}
