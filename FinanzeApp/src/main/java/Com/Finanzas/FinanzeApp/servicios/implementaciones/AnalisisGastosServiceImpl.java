package Com.Finanzas.FinanzeApp.servicios.implementaciones;

import Com.Finanzas.FinanzeApp.modelos.*;
import Com.Finanzas.FinanzeApp.repositorios.MovimientoRepository;
import Com.Finanzas.FinanzeApp.repositorios.PresupuestoCategoriaRepository;
import Com.Finanzas.FinanzeApp.servicios.interfaces.AnalisisGastosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class AnalisisGastosServiceImpl implements AnalisisGastosService {

    @Autowired
    private MovimientoRepository movimientoRepository;

    @Autowired
    private PresupuestoCategoriaRepository presupuestoRepo;

    @Override
    public List<GastoCategoriaDTO> obtenerAnalisis(Long usuarioId, LocalDate inicio, LocalDate fin) {
        List<Object[]> resultados = movimientoRepository.obtenerGastosPorCategoria(usuarioId, inicio, fin);

        double total = resultados.stream()
                .mapToDouble(r -> toDouble(r[1]))
                .sum();

        List<GastoCategoriaDTO> analisis = new ArrayList<>();
        for (Object[] fila : resultados) {
            String categoria = (String) fila[0];
            Double monto = toDouble(fila[1]); // Siempre devuelve un Double, nunca null
            Double porcentaje = total > 0 ? (monto / total) * 100.0 : 0.0; // Usa Double también
            analisis.add(new GastoCategoriaDTO(categoria, monto, porcentaje));
        }
        return analisis;
    }

    @Override
    public KPIAnalisisDTO obtenerKPIs(Long usuarioId, LocalDate inicio, LocalDate fin) {
        Double totalGastos = movimientoRepository.totalGastos(usuarioId, inicio, fin);
        if (totalGastos == null) totalGastos = 0.0;

        YearMonth ym = YearMonth.from(inicio);
        YearMonth prev = ym.minusMonths(1);
        LocalDate prevInicio = prev.atDay(1);
        LocalDate prevFin = prev.atEndOfMonth();

        Double totalPrev = movimientoRepository.totalGastosPeriodo(usuarioId, prevInicio, prevFin);
        if (totalPrev == null) totalPrev = 0.0;

        Double variacion = 0.0;
        if (totalPrev > 0) {
            variacion = ((totalGastos - totalPrev) / totalPrev) * 100.0;
        }

        long dias = java.time.temporal.ChronoUnit.DAYS.between(inicio, fin) + 1;
        Double promedioDiario = dias > 0 ? (totalGastos / dias) : 0.0;

        Double ingresos = movimientoRepository.totalIngresos(usuarioId, inicio, fin);
        if (ingresos == null) ingresos = 0.0;

        Double porcentajeAhorro = null;
        if (ingresos > 0) {
            Double ahorro = ingresos - totalGastos;
            porcentajeAhorro = (ahorro / ingresos) * 100.0;
        }

        List<GastoCategoriaDTO> lista = obtenerAnalisis(usuarioId, inicio, fin);
        String categoriaMasCostosa = lista.stream()
                .max(Comparator.comparing(GastoCategoriaDTO::getMonto, Comparator.nullsFirst(Double::compareTo)))
                .map(GastoCategoriaDTO::getCategoria)
                .orElse("-");

        return new KPIAnalisisDTO(totalGastos, categoriaMasCostosa, variacion, promedioDiario, porcentajeAhorro);
    }

    @Override
    public List<GastoMensualDTO> obtenerTendenciaMensual(Long usuarioId, LocalDate inicio, LocalDate fin) {
        List<Object[]> filas = movimientoRepository.gastoMensual(usuarioId, inicio, fin);
        List<GastoMensualDTO> out = new ArrayList<>();
        for (Object[] f : filas) {
            String periodo = String.valueOf(f[0]);
            Double total = toDouble(f[1]);
            out.add(new GastoMensualDTO(periodo, total));
        }
        return out;
    }

    @Override
    public List<TopGastoDTO> obtenerTopGastos(Long usuarioId, LocalDate inicio, LocalDate fin, int limite) {
        List<Object[]> filas = movimientoRepository.topGastos(usuarioId, inicio, fin, PageRequest.of(0, limite));
        List<TopGastoDTO> out = new ArrayList<>();
        for (Object[] o : filas) {
            String desc = (String) o[0];
            String cat = (String) o[1];
            LocalDate fecha = (LocalDate) o[2];
            Double monto = toDouble(o[3]);
            out.add(new TopGastoDTO(desc, cat, fecha, monto));
        }
        return out;
    }

    @Override
    public List<PresupuestoEstadoDTO> obtenerEstadoPresupuestos(Long usuarioId, LocalDate inicio, LocalDate fin) {
        var presupuestos = presupuestoRepo.findByUsuarioId(usuarioId);
        var gastosCategoria = obtenerAnalisis(usuarioId, inicio, fin);

        List<PresupuestoEstadoDTO> estados = new ArrayList<>();
        for (var p : presupuestos) {
            Double gasto = gastosCategoria.stream()
                    .filter(g -> g.getCategoria().equalsIgnoreCase(p.getCategoria()))
                    .map(GastoCategoriaDTO::getMonto)
                    .findFirst()
                    .orElse(0.0);

            Double presupuesto = p.getPresupuestoMensual() != null ? p.getPresupuestoMensual() : 0.0;
            boolean excedido = gasto > presupuesto;
            Double restante = presupuesto - gasto;
            Double progreso = presupuesto > 0 ? (gasto / presupuesto) * 100.0 : 0.0;

            estados.add(new PresupuestoEstadoDTO(
                    p.getCategoria(),
                    presupuesto,
                    gasto,
                    restante,
                    excedido,
                    progreso
            ));
        }
        return estados;
    }

    private Double toDouble(Object value) {
        if (value == null) return 0.0;
        if (value instanceof Double d) return d;
        if (value instanceof Number n) return n.doubleValue();
        try {
            return Double.parseDouble(value.toString());
        } catch (Exception e) {
            return 0.0;
        }
    }
}
