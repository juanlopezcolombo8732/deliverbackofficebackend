package ar.com.deliverar.deliver.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ar.com.deliverar.deliver.model.SueldoEntity;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
public class SueldoFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void liquidarSueldoConDosEntregas() throws Exception {
        // 1) Creo un usuario repartidor
        Map<String,Object> usuario = new HashMap<>();
        usuario.put("nombre", "Juan");
        usuario.put("apellido", "Perez");
        usuario.put("email", "juan.perez@example.com");
        usuario.put("telefono", "111222333");
        usuario.put("direccion", "Calle Falsa 123");
        usuario.put("ciudad", "Ciudad");
        usuario.put("pais", "Pais");
        usuario.put("rol", "REPARTIDOR");
        usuario.put("departamento", "Logística");

        usuario.put("fechaContratacion", "2025-06-01");
        usuario.put("salarioBase", 1000);
        usuario.put("porcentajeComision", 0.10);
        usuario.put("nombreContactoEmergencia", "Contacto");
        usuario.put("telefonoContactoEmergencia", "444555666");
        usuario.put("totalVentas", 0.0);
        usuario.put("saldoActual", 0.0);

        MvcResult r1 = mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(usuario)))
                .andReturn();
        Map<String,Object> uResp = mapper.readValue(r1.getResponse().getContentAsString(),
                new TypeReference<>() {});
        Long usuarioId = ((Number)uResp.get("id")).longValue();

        // 2) Creo un tenant (proveedor) vía evento tenant.created
        Map<String,Object> tenantPayload = new HashMap<>();

        tenantPayload.put("nombre", "Demo Tenant");
        tenantPayload.put("cuit", "30-12345678-9");
        tenantPayload.put("direccion", "Av. Siempre Viva 742");
        tenantPayload.put("email", "tenant@demo.com");
        tenantPayload.put("telefono", "999888777");
        tenantPayload.put("categoriaFiscal", "RESPONSABLE_INSCRIPTO");

        mockMvc.perform(post("/message")
                        .header("X-Event-Type", "tenant.created")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(tenantPayload)))
                .andReturn();

        // 3) Creo dos pedidos
        Map<String,Object> pedido1 = new HashMap<>();
        pedido1.put("pedidoId", "PED001");
        pedido1.put("tenantId", "TNT123");
        pedido1.put("repartidorId", usuarioId.toString());
        pedido1.put("estado", "ENTREGADO");
        pedido1.put("subtotal", 100.0);
        pedido1.put("moneda", "ARS");
        pedido1.put("createdAt", "2025-06-15T10:00:00Z");

        Map<String,Object> pedido2 = new HashMap<>();
        pedido2.put("pedidoId", "PED002");
        pedido2.put("tenantId", "TNT123");
        pedido2.put("repartidorId", usuarioId.toString());
        pedido2.put("estado", "ENTREGADO");
        pedido2.put("subtotal", 200.0);
        pedido2.put("moneda", "ARS");
        pedido2.put("createdAt", "2025-06-16T11:00:00Z");

        // Los guardo vía tu endpoint /api/pedidos
        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(pedido1)))
                .andReturn();
        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(pedido2)))
                .andReturn();

        // 4) Lanzo los eventos de entrega para que se creen los DetalleComision
        Map<String,Object> event1 = Map.of("pedidoId","PED001");
        Map<String,Object> event2 = Map.of("pedidoId","PED002");

        mockMvc.perform(post("/message")
                        .header("X-Event-Type", "pedido.entregado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(event1)))
                .andReturn();
        mockMvc.perform(post("/message")
                        .header("X-Event-Type", "pedido.entregado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(event2)))
                .andReturn();

        // 5) Ahora liquidamos el sueldo del repartidor para todo junio
        MvcResult r2 = mockMvc.perform(post("/api/sueldos/liquidar")
                        .param("usuarioId", usuarioId.toString())
                        .param("inicio", "2025-06-01")
                        .param("fin",    "2025-06-30"))
                .andReturn();

        // 6) Compruebo que el total de comisión sea (100*0.1 + 200*0.1) = 30.0
        SueldoEntity sueldo = mapper.readValue(r2.getResponse().getContentAsString(), SueldoEntity.class);
        assertThat(sueldo.getTotalComision()).isEqualTo(30.0);
        // Y que el total a pagar sume salarioBase + comisiones
        assertThat(sueldo.getTotalPagar()).isEqualTo(1030.0);

        // 7) Re-ejecutar la misma liquidación no suma de nuevo (detalles ya marcados)
        MvcResult r3 = mockMvc.perform(post("/api/sueldos/liquidar")
                        .param("usuarioId", usuarioId.toString())
                        .param("inicio", "2025-06-01")
                        .param("fin",    "2025-06-30"))
                .andReturn();
        SueldoEntity sueldo2 = mapper.readValue(r3.getResponse().getContentAsString(), SueldoEntity.class);
        assertThat(sueldo2.getTotalComision()).isEqualTo(0.0);
    }
}
