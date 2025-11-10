package Com.Finanzas.FinanzeApp.servicios.interfaces;


public interface MovimientosService {
    Double obtenerTotalIngresos(Long usuarioId);
    Double obtenerTotalEgresos(Long usuarioId);
    Double calcularBalance(Long usuarioId); // Nuevo método
}
