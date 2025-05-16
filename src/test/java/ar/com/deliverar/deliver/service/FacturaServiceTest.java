package ar.com.deliverar.deliver.service;

import ar.com.deliverar.deliver.model.Factura;
import ar.com.deliverar.deliver.model.Proveedor;
import ar.com.deliverar.deliver.repository.FacturaRepository;
import ar.com.deliverar.deliver.repository.ProveedorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FacturaServiceTest {

    @Mock
    private FacturaRepository facturaRepository;

    @Mock
    private ProveedorRepository proveedorRepository;

    @InjectMocks
    private FacturaService facturaService;

    private Proveedor proveedor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        proveedor = new Proveedor();
        proveedor.setId(1L);
        proveedor.setNombre("Proveedor Test");
    }

    @Test
    void testEmitirFactura() {
        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(proveedor));
        when(facturaRepository.save(any(Factura.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Factura factura = facturaService.emitirFactura(1L, "F001", 15000.0);

        assertNotNull(factura);
        assertEquals("F001", factura.getNumero());
        assertEquals(15000.0, factura.getMontoTotal());
        assertEquals(Factura.EstadoFactura.EMITIDA, factura.getEstado());
        assertEquals(proveedor, factura.getProveedor());

        verify(facturaRepository).save(factura);
    }

    @Test
    void testMarcarComoPagada() {
        Factura factura = new Factura();
        factura.setId(100L);
        factura.setEstado(Factura.EstadoFactura.EMITIDA);

        when(facturaRepository.findById(100L)).thenReturn(Optional.of(factura));
        when(facturaRepository.save(any(Factura.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Factura actualizada = facturaService.marcarComoPagada(100L);

        assertEquals(Factura.EstadoFactura.PAGADA, actualizada.getEstado());
        verify(facturaRepository).save(factura);
    }


}
