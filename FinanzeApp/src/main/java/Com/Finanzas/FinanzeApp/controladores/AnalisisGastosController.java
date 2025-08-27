package Com.Finanzas.FinanzeApp.controladores;

import Com.Finanzas.FinanzeApp.modelos.*;
import Com.Finanzas.FinanzeApp.servicios.interfaces.AnalisisGastosService;
import Com.Finanzas.FinanzeApp.servicios.interfaces.UsuarioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Controller
@RequestMapping("/analisis")
public class AnalisisGastosController {

    @Autowired
    private AnalisisGastosService analisisService;

    @Autowired
    private UsuarioServicio usuarioServicio;

    @GetMapping
    public String verAnalisis(Model model,
                              Authentication auth,
                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {

        if (auth == null || !auth.isAuthenticated()) return "redirect:/login";

        String correo = auth.getName();
        Long usuarioId = usuarioServicio.obtenerIdPorCorreo(correo);

        // Rango por defecto: mes actual
        if (desde == null || hasta == null) {
            YearMonth ym = YearMonth.now();
            desde = ym.atDay(1);
            hasta = ym.atEndOfMonth();
        }

        List<GastoCategoriaDTO> analisis = analisisService.obtenerAnalisis(usuarioId, desde, hasta);
        KPIAnalisisDTO kpis = analisisService.obtenerKPIs(usuarioId, desde, hasta);
        List<GastoMensualDTO> tendencia = analisisService.obtenerTendenciaMensual(
                usuarioId, YearMonth.now().minusMonths(5).atDay(1), YearMonth.now().atEndOfMonth());
        List<TopGastoDTO> top5 = analisisService.obtenerTopGastos(usuarioId, desde, hasta, 5);
        List<PresupuestoEstadoDTO> presupuestos = analisisService.obtenerEstadoPresupuestos(usuarioId, desde, hasta);

        // Para .js
        List<String> categorias = analisis.stream()
                .map(GastoCategoriaDTO::getCategoria)
                .toList();

        List<Double> totales = analisis.stream()
                .map(GastoCategoriaDTO::getMonto)
                .toList();

        List<String> meses = tendencia.stream()
                .map(GastoMensualDTO::getPeriodo)
                .toList();

        List<Double> totalesMensuales = tendencia.stream()
                .map(GastoMensualDTO::getTotal)
                .toList();

        model.addAttribute("desde", desde);
        model.addAttribute("hasta", hasta);
        model.addAttribute("analisis", analisis);
        model.addAttribute("categorias", categorias);
        model.addAttribute("totales", totales);
        model.addAttribute("kpis", kpis);
        model.addAttribute("meses", meses);
        model.addAttribute("totalesMensuales", totalesMensuales);
        model.addAttribute("top5", top5);
        model.addAttribute("presupuestos", presupuestos);

        // Mensaje simple de sugerencia con validación de null
        double umbral = 1000.0;
        Double totalGastos = kpis.getTotalGastos();
        String sugerencia = (totalGastos != null && totalGastos > umbral)
                ? "Este mes tus gastos están altos. Revisa ocio y suscripciones."
                : "Buen control de gastos. Mantén el ritmo.";

        model.addAttribute("sugerencia", sugerencia);

        return "analisisGastos";
    }

    @GetMapping("/export/csv")
    public void exportCsv(Authentication auth,
                          HttpServletResponse response,
                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) throws Exception {

        if (auth == null || !auth.isAuthenticated()) {
            response.sendRedirect("/login");
            return;
        }

        Long usuarioId = usuarioServicio.obtenerIdPorCorreo(auth.getName());
        List<GastoCategoriaDTO> analisis = analisisService.obtenerAnalisis(usuarioId, desde, hasta);

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=analisis_gastos.csv");

        try (PrintWriter writer = response.getWriter()) {
            writer.println("Categoria,Monto,Porcentaje");
            for (var a : analisis) {
                Double monto = a.getMonto();
                Double porcentaje = a.getPorcentaje();
                writer.printf("%s,%.2f,%.2f%n",
                        a.getCategoria(),
                        monto != null ? monto : 0.0,
                        porcentaje != null ? porcentaje : 0.0
                );
            }
        }
    }
}
