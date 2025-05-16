package ar.com.deliverar.deliver.service;

import ar.com.deliverar.deliver.model.Comision;
import ar.com.deliverar.deliver.model.OrdenPago;
import ar.com.deliverar.deliver.model.Usuario;
import ar.com.deliverar.deliver.repository.ComisionRepository;
import ar.com.deliverar.deliver.repository.OrdenPagoRepository;
import ar.com.deliverar.deliver.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrdenPagoServiceTest {

    @Mock
    private OrdenPagoRepository ordenPagoRepository;

    @Mock
    private ComisionRepository comisionRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private OrdenPagoService ordenPagoService;

    private Usuario repartidor;
    private Comision comision1;
    private Comision comision2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        repartidor = new Usuario();
        repartidor.setId(3L); // Asegurarse de usar el ID correcto esperado en el test

        comision1 = new Comision();
        comision1.setId(101L);
        comision1.setMonto(100.0);
        comision1.setEstado("PENDIENTE");
        comision1.setFecha(LocalDateTime.now());
        comision1.setRepartidor(repartidor);

        comision2 = new Comision();
        comision2.setId(102L);
        comision2.setMonto(150.0);
        comision2.setEstado("PENDIENTE");
        comision2.setFecha(LocalDateTime.now());
        comision2.setRepartidor(repartidor);
    }

    @Test
    void testGenerarOrdenPagoParaRepartidor() {
        List<Comision> comisiones = Arrays.asList(comision1, comision2);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(repartidor));
        when(comisionRepository.findByRepartidorIdAndOrdenPagoIsNull(1L)).thenReturn(comisiones);
        when(ordenPagoRepository.save(any(OrdenPago.class))).thenAnswer(invocation -> {
            OrdenPago orden = invocation.getArgument(0);
            orden.setComisiones(comisiones); // 💡 nos aseguramos que no sea null
            return orden;
        });

        OrdenPago orden = ordenPagoService.generarOrdenPagoParaRepartidor(1L);

        assertNotNull(orden);
        assertNotNull(orden.getComisiones(), "La lista de comisiones no debería ser null");
        assertEquals(2, orden.getComisiones().size());
        assertEquals(250.0, orden.getMontoTotal());
        assertEquals("PENDIENTE", orden.getEstado());
        assertEquals(repartidor, orden.getRepartidor());

        verify(comisionRepository, times(1)).findByRepartidorIdAndOrdenPagoIsNull(1L);
        verify(ordenPagoRepository, times(1)).save(any(OrdenPago.class));
    }

    @Test
    void testMarcarOrdenComoPagada() {
        OrdenPago orden = new OrdenPago();
        orden.setId(200L);
        orden.setEstado("PENDIENTE");

        when(ordenPagoRepository.findById(200L)).thenReturn(Optional.of(orden));
        when(ordenPagoRepository.save(any(OrdenPago.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrdenPago resultado = ordenPagoService.marcarComoPagada(200L);

        assertNotNull(resultado);
        assertEquals("PAGADA", resultado.getEstado());
        verify(ordenPagoRepository).save(orden);
    }
}
