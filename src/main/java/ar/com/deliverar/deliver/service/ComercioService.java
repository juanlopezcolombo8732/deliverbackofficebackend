package ar.com.deliverar.deliver.service;

import ar.com.deliverar.deliver.model.Comercio;
import ar.com.deliverar.deliver.model.Proveedor;
import ar.com.deliverar.deliver.repository.ComercioRepository;
import ar.com.deliverar.deliver.repository.ProveedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComercioService {

    private final ComercioRepository comercioRepo;
    private final ProveedorRepository proveedorRepo;


    @Autowired
    public ComercioService(ComercioRepository comercioRepo,
                           ProveedorRepository proveedorRepo) {
        this.comercioRepo   = comercioRepo;
        this.proveedorRepo  = proveedorRepo;
    }

    public Comercio upsertFromEvent(String comercioId, String tenantId, String calle, String numero) {
        Proveedor tenant = proveedorRepo.findByExternalId(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant no encontrado: " + tenantId));


        Comercio nc = new Comercio();
        nc.setId(comercioId);


        nc.setTenant(tenant);
        nc.setCalle(calle);
        nc.setNumero(numero);

        return comercioRepo.save(nc);
    }

    public Comercio findById(String id) {
        return comercioRepo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Comercio no encontrado: " + id)
                );
    }
}
