package Com.Finanzas.FinanzeApp.servicios.interfaces;

import Com.Finanzas.FinanzeApp.modelos.Movimiento;

import java.time.LocalDate;
import java.util.List;

public interface MovimientoService {
    Double obtenerTotalIngresos(Long usuarioId);
    Double obtenerTotalEgresos(Long usuarioId);
    Double calcularBalance(Long usuarioId);
    Movimiento guardarMovimiento(Movimiento movimiento); // ✅ agregado
    // CRUD básico
    Movimiento obtenerPorId(Long id);
    void eliminarMovimiento(Long id);
    List<Movimiento> listarPorUsuario(Long usuarioId);
    List<Movimiento> listarPorUsuarioYRango(Long usuarioId, LocalDate inicio, LocalDate fin);

}
