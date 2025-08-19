package Com.Finanzas.FinanzeApp.servicios.interfaces;

import Com.Finanzas.FinanzeApp.modelos.GastoCategoriaDTO;
import java.util.List;


import Com.Finanzas.FinanzeApp.modelos.*;
import java.time.LocalDate;

public interface AnalisisGastosService {
    List<GastoCategoriaDTO> obtenerAnalisis(Long usuarioId, LocalDate inicio, LocalDate fin);
    KPIAnalisisDTO obtenerKPIs(Long usuarioId, LocalDate inicio, LocalDate fin);
    List<GastoMensualDTO> obtenerTendenciaMensual(Long usuarioId, LocalDate inicio, LocalDate fin);
    List<TopGastoDTO> obtenerTopGastos(Long usuarioId, LocalDate inicio, LocalDate fin, int limite);
    List<PresupuestoEstadoDTO> obtenerEstadoPresupuestos(Long usuarioId, LocalDate inicio, LocalDate fin);
}

