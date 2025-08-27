package Com.Finanzas.FinanzeApp.servicios.implementaciones;

import Com.Finanzas.FinanzeApp.modelos.Meta;
import Com.Finanzas.FinanzeApp.modelos.Movimiento;
import Com.Finanzas.FinanzeApp.modelos.EstadoMeta;
import Com.Finanzas.FinanzeApp.repositorios.MovimientoRepository;
import Com.Finanzas.FinanzeApp.servicios.interfaces.MetaService;
import Com.Finanzas.FinanzeApp.servicios.interfaces.MovimientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MovimientoServiceImpl implements MovimientoService {

    @Autowired
    private MovimientoRepository movimientoRepository;

    @Autowired
    private MetaService metaService;

    @Override
    public Double obtenerTotalIngresos(Long usuarioId) {
        Double total = movimientoRepository.totalIngresos(usuarioId);
        return total != null ? total : 0.0;
    }

    @Override
    public Double obtenerTotalEgresos(Long usuarioId) {
        Double total = movimientoRepository.totalEgresos(usuarioId);
        return total != null ? total : 0.0;
    }

    @Override
    public Double calcularBalance(Long usuarioId) {
        Double ingresos = obtenerTotalIngresos(usuarioId);
        Double egresos = obtenerTotalEgresos(usuarioId);
        return ingresos - egresos;
    }

    // 🟢 CREATE / UPDATE
    @Override
    public Movimiento guardarMovimiento(Movimiento movimiento) {
        Movimiento movGuardado = movimientoRepository.save(movimiento);

        // Verificar si la categoría tiene meta asociada
        if (movGuardado.getCategoria() != null && movGuardado.getCategoria().getMeta() != null) {
            Meta meta = movGuardado.getCategoria().getMeta();

            if (meta.getEstado() != EstadoMeta.COMPLETADA) {
                meta.setMontoActual(meta.getMontoActual() + movGuardado.getMonto());
                metaService.guardarMeta(meta); // reutilizamos lógica de metas
            }
        }

        return movGuardado;
    }
    @Override
    public List<Movimiento> listarPorUsuarioYRango(Long usuarioId, LocalDate inicio, LocalDate fin) {
        return movimientoRepository.findByUsuarioIdAndFechaBetween(usuarioId, inicio, fin);
    }

    // 🔵 READ por id
    @Override
    public Movimiento obtenerPorId(Long id) {
        return movimientoRepository.findById(id).orElse(null);
    }

    // 🔴 DELETE
    @Override
    public void eliminarMovimiento(Long id) {
        movimientoRepository.deleteById(id);
    }

    // 🔵 READ listar por usuario
    @Override
    public List<Movimiento> listarPorUsuario(Long usuarioId) {
        return movimientoRepository.findByUsuarioId(usuarioId);
    }
}
