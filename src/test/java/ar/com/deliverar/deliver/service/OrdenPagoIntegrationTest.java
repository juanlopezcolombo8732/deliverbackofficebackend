package ar.com.deliverar.deliver.service;

import ar.com.deliverar.deliver.model.Comision;
import ar.com.deliverar.deliver.model.OrdenPago;
import ar.com.deliverar.deliver.model.Usuario;
import ar.com.deliverar.deliver.repository.ComisionRepository;
import ar.com.deliverar.deliver.repository.OrdenPagoRepository;
import ar.com.deliverar.deliver.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class OrdenPagoIntegrationTest {

    @Autowired
    private OrdenPagoService ordenPagoService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ComisionRepository comisionRepository;

    @Autowired
    private OrdenPagoRepository ordenPagoRepository;

    private Usuario repartidor;

    @BeforeEach
    void setUp() {
        // Limpieza
        comisionRepository.deleteAll();
        ordenPagoRepository.deleteAll();
        usuarioRepository.deleteAll();

        repartidor = new Usuario();
        repartidor.setNombre("Carlos");
        repartidor.setApellido("González");
        repartidor.setEmail("carlos.gonzalez@example.com");
        repartidor.setRol("REPARTIDOR");
        repartidor.setTelefono("1010101010");

        repartidor = usuarioRepository.save(repartidor);
    }

    @Test
    void testFlujoCompletoDeLiquidacion() {
        // Crear un repartidor
        Usuario repartidor = new Usuario();
        repartidor.setNombre("Carlos");
        repartidor.setRol("Repartidor");
        repartidor = usuarioRepository.save(repartidor);

        // Crear comisiones pendientes
        Comision c1 = new Comision();
        c1.setRepartidor(repartidor);
        c1.setFecha(LocalDateTime.now());
        c1.setMonto(100.0);
        c1.setEstado("PENDIENTE");

        Comision c2 = new Comision();
        c2.setRepartidor(repartidor);
        c2.setFecha(LocalDateTime.now());
        c2.setMonto(150.0);
        c2.setEstado("PENDIENTE");

        comisionRepository.saveAll(List.of(c1, c2));

        // Generar la orden de pago
        OrdenPago orden = ordenPagoService.generarOrdenPagoParaRepartidor(repartidor.getId());

        // ⚠️ Recargar desde la base para asegurar que traiga la relación comisiones
        orden = ordenPagoRepository.findById(orden.getId()).orElseThrow();

        // Validaciones
        assertNotNull(orden);
        assertEquals(2, orden.getComisiones().size());
        assertEquals("PENDIENTE", orden.getEstado());
        assertEquals(250.0, orden.getMontoTotal());
        assertEquals(repartidor.getId(), orden.getRepartidor().getId());

        // Verificar que las comisiones ahora tienen orden de pago asociada
        for (Comision com : orden.getComisiones()) {
            assertEquals(orden.getId(), com.getOrdenPago().getId());
        }
    }

}
