package Com.Finanzas.FinanzeApp.servicios.implementaciones;

import Com.Finanzas.FinanzeApp.modelos.Meta;
import Com.Finanzas.FinanzeApp.modelos.Movimiento;
import Com.Finanzas.FinanzeApp.modelos.EstadoMeta;
import Com.Finanzas.FinanzeApp.repositorios.MovimientoRepository;
import Com.Finanzas.FinanzeApp.servicios.interfaces.MetaService;
import Com.Finanzas.FinanzeApp.servicios.interfaces.MovimientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    // ✅ Nuevo: guardar movimiento + actualizar meta automáticamente
    public Movimiento guardarMovimiento(Movimiento movimiento) {
        Movimiento movGuardado = movimientoRepository.save(movimiento);

        // Verificar si la categoría tiene meta asociada
        if (movGuardado.getCategoria() != null && movGuardado.getCategoria().getMeta() != null) {
            Meta meta = movGuardado.getCategoria().getMeta();

            if (meta.getEstado() != EstadoMeta.COMPLETADA) {
                // Sumar al monto actual de la meta
                meta.setMontoActual(meta.getMontoActual() + movGuardado.getMonto());

                // ⚡ Reutilizamos la lógica de MetaService para verificar si se completa
                metaService.guardarMeta(meta);
            }
        }

        return movGuardado;
    }
}
