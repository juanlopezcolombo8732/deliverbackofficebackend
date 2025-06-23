package ar.com.deliverar.deliver.controller;

import ar.com.deliverar.deliver.service.ComisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    private final ComisionService comisionService;

    @Autowired
    public TestController(ComisionService comisionService) {
        this.comisionService = comisionService;
    }

    /**
     * Simula la recepción de un pedido entregado sin usar Kafka.
     */
    @PostMapping("/pedido-entregado-local")
    public ResponseEntity<Void> simularPedidoEntregado(@RequestBody Map<String, String> body) {
        String pedidoId = body.get("pedidoId");
        comisionService.procesarPedidoEntregado(pedidoId);
        return ResponseEntity.ok().build();
    }
}
