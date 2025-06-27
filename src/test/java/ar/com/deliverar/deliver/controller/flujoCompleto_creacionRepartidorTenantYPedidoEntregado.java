package ar.com.deliverar.deliver.controller;

import ar.com.deliverar.deliver.model.*;
import ar.com.deliverar.deliver.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FullFlowIntegrationTest {

    @Autowired MockMvc                      mvc;
    @Autowired ObjectMapper                 mapper;
    @Autowired UsuarioRepository            usuarioRepo;
    @Autowired ProveedorRepository          proveedorRepo;
    @Autowired PedidoRepository             pedidoRepo;
    @Autowired DetalleComisionRepository    detalleRepo;

    @BeforeEach
    void clean() {
        detalleRepo.deleteAll();
        pedidoRepo.deleteAll();
    }
/*
    @Test
    void flujoCompleto_creacionRepartidorTenantYPedidoEntregado() throws Exception {
        // 1) Llego evento de repartidor
        Map<String,Object> repPayload = new HashMap<>();
        repPayload.put("repartidorId", "U55");
        repPayload.put("nombre",        "Juan");
        repPayload.put("apellido",      "Perez");
        repPayload.put("email",         "juan.perez@ej.com");
        repPayload.put("telefono",      "555-1234");
        Map<String,Object> repEvt = Map.of("payload", repPayload);

        mvc.perform(post("/message")
                        .header("X-Event-Type","delivery.nuevoRepartidor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(repEvt)))
                .andExpect(status().isOk());

        Usuario user = usuarioRepo.findByExternalId("U42")
                .orElseThrow(() -> new AssertionError("Usuario no creado"));
        assertThat(user.getNombre()).isEqualTo("Juan");
        assertThat(user.getRol()).isEqualTo("REPARTIDOR");

        // 2) Llego evento de tenant
        Map<String,Object> tenantEvt = Map.of(
                "id",               "T99",
                "nombre",           "Acme Ltda",
                "cuit",             "30-99999999-9",
                "direccion",        "Calle Falsa 123",
                "email",            "contacto@acme.com",
                "telefono",         "555-9999",
                "categoriaFiscal",  "RESPONSABLE_INSCRIPTO"
        );
        mvc.perform(post("/message")
                        .header("X-Event-Type","tenant.created")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(tenantEvt)))
                .andExpect(status().isOk());

        Proveedor prov = proveedorRepo.findByExternalId("T99")
                .orElseThrow(() -> new AssertionError("Proveedor no creado"));
        assertThat(prov.getNombre()).isEqualTo("Acme Ltda");

        // 3) Llego evento pedido.entregado
        Map<String,Object> orderEvt = new HashMap<>();
        orderEvt.put("pedidoId",     "P123");
        orderEvt.put("subtotal",     250.0);
        orderEvt.put("moneda",       "ARS");
        orderEvt.put("createdAt",    Instant.parse("2025-06-25T12:00:00Z"));
        orderEvt.put("tenantId",     "T19");
        orderEvt.put("repartidorId", "U42");

        mvc.perform(post("/message")
                        .header("X-Event-Type","pedido.entregado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(orderEvt)))
                .andExpect(status().isOk());

        // Verifico que se creó el pedido
        Pedido pedido = pedidoRepo.findById("P123")
                .orElseThrow(() -> new AssertionError("Pedido no creado"));
        assertThat(pedido.getSubtotal()).isEqualTo(250.0);
        assertThat(pedido.getTenant().getId()).isEqualTo(prov.getId());
        assertThat(pedido.getRepartidorId()).isEqualTo("U42");

        // ——————————————————————————————————————
        // 4) Verifico detalle de comisión y totales del repartidor
        // ——————————————————————————————————————

        List<DetalleComision> detalles = detalleRepo.findAll();
        assertThat(detalles).hasSize(1);

        DetalleComision det = detalles.get(0);
        assertThat(det.getPedidoId()).isEqualTo("P123");

        // El porcentaje por defecto de Usuario es 0.2 → 250 * 0.2 = 50.0
        double esperado = 250.0 * user.getPorcentajeComision();
        assertThat(det.getMonto()).isEqualTo(esperado);
        assertThat(det.getUsuario().getExternalId()).isEqualTo("U42");
        assertThat(det.isLiquidado()).isFalse();

        // El usuario debe haber acumulado totalVentas y saldoActual
        Usuario updated = usuarioRepo.findByExternalId("U42")
                .orElseThrow();
        assertThat(updated.getTotalVentas()).isEqualTo(250.0);
        assertThat(updated.getSaldoActual()).isEqualTo(esperado);
    }

 */
}
