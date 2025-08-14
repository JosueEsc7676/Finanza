package Com.Finanzas.FinanzeApp.servicios.implementaciones;

import Com.Finanzas.FinanzeApp.repositorios.MovimientoRepository;
import Com.Finanzas.FinanzeApp.servicios.interfaces.MovimientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MovimientoServiceImpl implements MovimientoService {

    @Autowired
    private MovimientoRepository movimientoRepository;

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
}
