//package Com.Finanzas.FinanzeApp.controladores;
//
//import Com.Finanzas.FinanzeApp.modelos.Movimiento;
//import Com.Finanzas.FinanzeApp.modelos.Usuario;
//import Com.Finanzas.FinanzeApp.repositorios.MovimientoRepository;
//import Com.Finanzas.FinanzeApp.servicios.interfaces.UsuarioServicio;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.time.LocalDate;
//import java.time.YearMonth;
//import java.util.List;
//import java.util.Optional;
//
//@RequestMapping("/")
//@Controller
//public class HomeController {
//
//    @Autowired
//    private MovimientoRepository movimientoRepository;
//
//    @Autowired
//    private UsuarioServicio usuarioServicio;
//
//    private Usuario getUsuarioAutenticado() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null && authentication.isAuthenticated()) {
//            String username = authentication.getName();
//            Optional<Usuario> usuarioOpt = usuarioServicio.buscarPorCorreo(username);
//            return usuarioOpt.orElse(null);
//        }
//        return null;
//    }
//
//    @GetMapping("/inicio")
//    public String mostrarInicio(Model model) {
//        Usuario usuario = getUsuarioAutenticado();
//        if (usuario == null) {
//            return "redirect:/login";
//        }
//
//        Double totalIngresosDouble = movimientoRepository.totalIngresos(usuario.getId());
//        Double totalEgresosDouble = movimientoRepository.totalEgresos(usuario.getId());
//
//        BigDecimal totalIngresos = BigDecimal.valueOf(totalIngresosDouble != null ? totalIngresosDouble : 0.0);
//        BigDecimal totalEgresos = BigDecimal.valueOf(totalEgresosDouble != null ? totalEgresosDouble : 0.0);
//        BigDecimal balance = totalIngresos.subtract(totalEgresos);
//
//        List<Movimiento> ultimosMovimientos = movimientoRepository.findTop4ByUsuarioIdOrderByFechaDesc(usuario.getId());
//
//        model.addAttribute("usuario", usuario);
//        model.addAttribute("totalIngresos", "$" + totalIngresos.toString());
//        model.addAttribute("totalEgresos", "$" + totalEgresos.toString());
//        model.addAttribute("balance", "$" + balance.toString());
//        model.addAttribute("porcentajeIngresos", null);
//        model.addAttribute("porcentajeEgresos", null);
//        model.addAttribute("movimientos", ultimosMovimientos);
//        model.addAttribute("mostrarControles", true);
//
//        return "inicio";
//    }
//
//    @GetMapping("/login")
//    public String mostrarLogin() {
//        return "login";
//    }
//}
