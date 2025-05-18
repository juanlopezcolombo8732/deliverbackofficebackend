package ar.com.deliverar.deliver.service;
import ar.com.deliverar.deliver.model.EstadoLiquidacion;
import ar.com.deliverar.deliver.model.Liquidacion;
import ar.com.deliverar.deliver.model.Usuario;
import ar.com.deliverar.deliver.repository.LiquidacionRepository;
import ar.com.deliverar.deliver.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LiquidacionService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private LiquidacionRepository liquidacionRepository;

    // Este es el que llama el controller con /generar
    public List<Liquidacion> generarLiquidaciones() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        List<Liquidacion> resultados = new ArrayList<>();

        for (Usuario u : usuarios) {
            double ventas = Optional.ofNullable(u.getTotalVentas()).orElse(0.0);
            double porcentaje = Optional.ofNullable(u.getPorcentajeComision()).orElse(0.0);
            double comision = ventas * (porcentaje / 100);
            double salario = Optional.ofNullable(u.getSalarioBase()).orElse(0.0);
            double total = salario + comision;

            u.setSaldoActual(Optional.ofNullable(u.getSaldoActual()).orElse(0.0) + total);
            usuarioRepository.save(u);

            Liquidacion liq = new Liquidacion();
            liq.setUsuario(u);
            liq.setFechaLiquidacion(LocalDate.now());
            liq.setComisionCalculada(comision);
            liq.setMontoTotalLiquidado(total);
            liquidacionRepository.save(liq);

            resultados.add(liq);
        }

        return resultados;
    }

    public Optional<Liquidacion> actualizarEstado(Long id, EstadoLiquidacion estado) {
        Optional<Liquidacion> liquidacionOpt = liquidacionRepository.findById(id);
        if (liquidacionOpt.isPresent()) {
            Liquidacion liquidacion = liquidacionOpt.get();
            liquidacion.setEstado(estado);
            return Optional.of(liquidacionRepository.save(liquidacion));
        }
        return Optional.empty();
    }


    // Este es el que llama el controller con GET /api/liquidaciones
    public List<Liquidacion> obtenerTodas() {
        return liquidacionRepository.findAll();
    }

    public List<Liquidacion> obtenerHistorialPorUsuario(Long usuarioId) {
        return liquidacionRepository.findByUsuarioId(usuarioId);
    }
}
