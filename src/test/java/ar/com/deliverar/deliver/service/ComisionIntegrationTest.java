package ar.com.deliverar.deliver.service;

import ar.com.deliverar.deliver.model.Comision;
import ar.com.deliverar.deliver.model.Usuario;
import ar.com.deliverar.deliver.repository.ComisionRepository;
import ar.com.deliverar.deliver.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ComisionIntegrationTest {

    @Autowired
    private ComisionRepository comisionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void testCrearComisionRealEnMySQL() {
        // Crear usuario dummy si no existe
        Usuario repartidor = new Usuario();
        repartidor.setNombre("Carlos");
        repartidor.setApellido("Lopez");
        repartidor.setEmail("carlos.lopez@example.com");
        repartidor.setRol("REPARTIDOR");
        repartidor.setTelefono("123456789");

        repartidor = usuarioRepository.save(repartidor);

        // Crear comisión real
        Comision comision = new Comision();
        comision.setFecha(LocalDateTime.now());
        comision.setMonto(100.0);
        comision.setEstado("PENDIENTE");
        comision.setRepartidor(repartidor);

        Comision guardada = comisionRepository.save(comision);

        assertNotNull(guardada.getId());
        assertEquals("PENDIENTE", guardada.getEstado());
        assertEquals(100.0, guardada.getMonto());
        assertEquals(repartidor.getId(), guardada.getRepartidor().getId());
    }

    @Test
    void testBuscarComisionesPendientesPorRepartidor() {
        // Crear un repartidor
        Usuario repartidor = new Usuario();
        repartidor.setNombre("Laura");
        repartidor.setApellido("Martinez");
        repartidor.setEmail("laura.martinez@example.com");
        repartidor.setRol("REPARTIDOR");
        repartidor.setTelefono("987654321");

        repartidor = usuarioRepository.save(repartidor);

        // Crear dos comisiones pendientes sin orden de pago
        Comision comision1 = new Comision();
        comision1.setFecha(LocalDateTime.now());
        comision1.setMonto(200.0);
        comision1.setEstado("PENDIENTE");
        comision1.setRepartidor(repartidor);

        Comision comision2 = new Comision();
        comision2.setFecha(LocalDateTime.now());
        comision2.setMonto(150.0);
        comision2.setEstado("PENDIENTE");
        comision2.setRepartidor(repartidor);

        comisionRepository.save(comision1);
        comisionRepository.save(comision2);

        // Ejecutar metodo real
        var pendientes = comisionRepository.findByRepartidorIdAndOrdenPagoIsNull(repartidor.getId());

        assertFalse(pendientes.isEmpty());
        assertEquals(2, pendientes.size());
        pendientes.forEach(c -> {
            assertNull(c.getOrdenPago());
            assertEquals("PENDIENTE", c.getEstado());
        });
    }

}
