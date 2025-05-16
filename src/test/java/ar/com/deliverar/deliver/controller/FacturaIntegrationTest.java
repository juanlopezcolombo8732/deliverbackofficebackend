package ar.com.deliverar.deliver.controller;

import ar.com.deliverar.deliver.model.Factura;
import ar.com.deliverar.deliver.model.Proveedor;
import ar.com.deliverar.deliver.repository.FacturaRepository;
import ar.com.deliverar.deliver.repository.ProveedorRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class FacturaIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProveedorRepository proveedorRepository;

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private ObjectMapper objectMapper; // ✅ usar el ObjectMapper de Spring

    private Long proveedorId;

    @BeforeEach
    void setUp() {
        facturaRepository.deleteAll();
        proveedorRepository.deleteAll();

        Proveedor proveedor = new Proveedor();
        proveedor.setNombre("Proveedor Demo");
        proveedor.setCuit("30-12345678-9");
        proveedor.setDireccion("Calle Falsa 123");
        proveedor.setEmail("proveedor@demo.com");
        proveedor.setTelefono("123456789");
        proveedor.setCategoriaFiscal("Responsable Inscripto");

        proveedorId = proveedorRepository.save(proveedor).getId();
    }

    @Test
    void flujoCrearYMarcarFacturaComoPagada() throws Exception {
        String emitirUrl = "/api/facturas/emitir?proveedorId=" + proveedorId +
                "&numero=F0001-00012345&montoTotal=15000.0";

        String response = mockMvc.perform(post(emitirUrl))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("EMITIDA"))
                .andExpect(jsonPath("$.numero").value("F0001-00012345"))
                .andReturn().getResponse().getContentAsString();

        Factura factura = objectMapper.readValue(response, Factura.class); // ✅ este sí funciona

        mockMvc.perform(patch("/api/facturas/" + factura.getId() + "/estado?estado=PAGADA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("PAGADA"));
    }
}
