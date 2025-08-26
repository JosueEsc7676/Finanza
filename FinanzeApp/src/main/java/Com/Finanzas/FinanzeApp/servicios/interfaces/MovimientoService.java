package Com.Finanzas.FinanzeApp.servicios.interfaces;

import Com.Finanzas.FinanzeApp.modelos.Movimiento;

public interface MovimientoService {
    Double obtenerTotalIngresos(Long usuarioId);
    Double obtenerTotalEgresos(Long usuarioId);
    Double calcularBalance(Long usuarioId);
    Movimiento guardarMovimiento(Movimiento movimiento); // ✅ agregado
}
