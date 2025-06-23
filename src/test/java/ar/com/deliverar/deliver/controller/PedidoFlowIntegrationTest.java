package ar.com.deliverar.deliver.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class PedidoFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void fullPurchaseFlowTest() throws Exception {
        // 1. Crear Usuario
        Map<String, Object> usuario = new HashMap<>();
        usuario.put("nombre", "Test");
        usuario.put("apellido", "User");
        usuario.put("email", "test.user@example.com");
        usuario.put("telefono", "1111111111");
        usuario.put("direccion", "Calle Test 123");
        usuario.put("ciudad", "Ciudad");
        usuario.put("pais", "Pais");
        usuario.put("rol", "REPARTIDOR");
        usuario.put("departamento", "Logística");
        usuario.put("fechaContratacion", "2025-06-22");
        usuario.put("salarioBase", 30000.0);
        usuario.put("porcentajeComision", 0.10);
        usuario.put("nombreContactoEmergencia", "Contacto");
        usuario.put("telefonoContactoEmergencia", "1199999999");
        usuario.put("totalVentas", 0.0);
        usuario.put("saldoActual", 0.0);

        MvcResult resUser = mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isOk())
                .andReturn();
        Map<?,?> createdUser = objectMapper.readValue(resUser.getResponse().getContentAsString(), Map.class);
        assertThat(createdUser.get("id")).isNotNull();

        // 2. Crear Proveedor
        Map<String, Object> proveedor = new HashMap<>();
        proveedor.put("nombre", "TenantTest");
        proveedor.put("cuit", "30123456789");
        proveedor.put("direccion", "Av Tenant 100");
        proveedor.put("email", "tenant@test.com");
        proveedor.put("telefono", "2222222222");
        proveedor.put("categoriaFiscal", "RESPONSABLE_INSCRIPTO");

        MvcResult resProv = mockMvc.perform(post("/api/proveedores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(proveedor)))
                .andExpect(status().isOk())
                .andReturn();
        Map<?,?> createdProv = objectMapper.readValue(resProv.getResponse().getContentAsString(), Map.class);
        Long provId = ((Number)createdProv.get("id")).longValue();

        // 3. Crear Pedido
        Map<String, Object> innerTenant = new HashMap<>();
        innerTenant.put("id", provId);
        Map<String, Object> pedido = new HashMap<>();
        pedido.put("pedidoId", "TEST_001");
        pedido.put("tenant", innerTenant);
        pedido.put("repartidorId", createdUser.get("id").toString());
        pedido.put("estado", "CREADO");
        pedido.put("subtotal", 1000.0);
        pedido.put("moneda", "ARS");
        pedido.put("createdAt", "2025-06-23T00:00:00Z");

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedido)))
                .andExpect(status().isOk());

        // 4. Simular pedido entregado
        Map<String, String> entregaBody = new HashMap<>();
        entregaBody.put("pedidoId", "TEST_001");
        mockMvc.perform(post("/api/test/pedido-entregado-local")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entregaBody)))
                .andExpect(status().isOk());

        // 5. Verificar comisión creada
        MvcResult resCom = mockMvc.perform(get("/api/comisiones"))
                .andExpect(status().isOk())
                .andReturn();
        List<?> comList = objectMapper.readValue(resCom.getResponse().getContentAsString(), List.class);
        assertThat(comList).isNotEmpty();

        // 6. Generar facturas por periodo
        mockMvc.perform(post("/api/facturas/generar-periodo?inicio=2025-06-01&fin=2025-06-30"))
                .andExpect(status().isOk());

        // 7. Listar facturas y validar
        MvcResult resFac = mockMvc.perform(get("/api/facturas"))
                .andExpect(status().isOk())
                .andReturn();
        List<?> facList = objectMapper.readValue(resFac.getResponse().getContentAsString(), List.class);
        assertThat(facList).isNotEmpty();
    }
}
