package Com.Finanzas.FinanzeApp.controladores;

import Com.Finanzas.FinanzeApp.modelos.Movimiento;
import Com.Finanzas.FinanzeApp.modelos.Usuario;
import Com.Finanzas.FinanzeApp.repositorios.MovimientoRepository;
import Com.Finanzas.FinanzeApp.repositorios.UsuarioRepositorio;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class LoginController {

    private final UsuarioRepositorio usuarioRepositorio;
    private final MovimientoRepository movimientoRepository;

    public LoginController(UsuarioRepositorio usuarioRepositorio, MovimientoRepository movimientoRepository) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.movimientoRepository = movimientoRepository;
    }

    @GetMapping("/")
    public String redirigirRaiz() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String mostrarLogin() {
        return "Login";
    }

    @GetMapping("/inicio")
    public String mostrarInicio(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String correo = auth.getName();

        Usuario usuario = usuarioRepositorio.findByCorreo(correo).orElse(null);

        if (usuario != null) {
            model.addAttribute("usuario", usuario);

            Double totalIngresos = movimientoRepository.totalIngresos(usuario.getId());
            Double totalEgresos = movimientoRepository.totalEgresos(usuario.getId());

            totalIngresos = totalIngresos != null ? totalIngresos : 0.0;
            totalEgresos = totalEgresos != null ? totalEgresos : 0.0;
            double balance = totalIngresos - totalEgresos;

            model.addAttribute("totalIngresos", totalIngresos);
            model.addAttribute("totalEgresos", totalEgresos);
            model.addAttribute("balance", balance);

            // Agregar los movimientos del usuario al modelo
            List<Movimiento> movimientosRecientes = movimientoRepository
                    .findTop4ByUsuarioIdOrderByFechaDesc(usuario.getId());
            model.addAttribute("movimientos", movimientosRecientes);

            // ✅ Se pasa el valor al layout
            model.addAttribute("modoOscuro", Boolean.TRUE.equals(usuario.getModoOscuro()));
        } else {
            return "redirect:/login";
        }

        return "inicio";
    }


}
