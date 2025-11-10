package Com.Finanzas.FinanzeApp.controladores;

import Com.Finanzas.FinanzeApp.modelos.Usuario;
import Com.Finanzas.FinanzeApp.repositorios.MovimientoRepository;
import Com.Finanzas.FinanzeApp.repositorios.UsuarioRepositorio;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Base64;
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
        return "login";
    }



    @GetMapping("/inicio")
    public String mostrarInicio(Model model,
                                HttpSession session,
                                @AuthenticationPrincipal OAuth2User oauthUser,
                                Authentication authentication) {
        String correo = null;

        // --- Login Google ---
        if (oauthUser != null) {
            correo = oauthUser.getAttribute("email");
            Usuario usuarioExistente = usuarioRepositorio.findByCorreo(correo).orElse(null);
            if (usuarioExistente == null) {
                Usuario nuevo = new Usuario();
                nuevo.setCorreo(correo);
                nuevo.setNombreCompleto(oauthUser.getAttribute("name"));
                nuevo.setContrasena("{noop}GOOGLE");
                nuevo.setProveedor("GOOGLE");
                nuevo.setProveedorId(oauthUser.getAttribute("sub"));
                nuevo.setFotoUrl(oauthUser.getAttribute("picture"));
                usuarioRepositorio.save(nuevo);
            }
        } else {
            // --- Login normal ---
            correo = authentication.getName();
        }

        if (correo == null) {
            return "redirect:/login?error=true";
        }

        Usuario usuario = usuarioRepositorio.findByCorreo(correo).orElse(null);
        if (usuario == null) {
            return "redirect:/login";
        }

        // ✅ Usuario al modelo
        model.addAttribute("usuario", usuario);



        // ✅ Movimientos y balance
        Double totalIngresos = movimientoRepository.totalIngresos(usuario.getId());
        Double totalEgresos = movimientoRepository.totalEgresos(usuario.getId());
        totalIngresos = totalIngresos != null ? totalIngresos : 0.0;
        totalEgresos = totalEgresos != null ? totalEgresos : 0.0;

        model.addAttribute("totalIngresos", totalIngresos);
        model.addAttribute("totalEgresos", totalEgresos);
        model.addAttribute("balance", totalIngresos - totalEgresos);
        model.addAttribute("movimientos",
                movimientoRepository.findTop4ByUsuarioIdOrderByFechaDesc(usuario.getId()));
        model.addAttribute("modoOscuro", Boolean.TRUE.equals(usuario.getModoOscuro()));

        // ✅ Notificación (solo una vez)
        if (Boolean.TRUE.equals(session.getAttribute("mostrarNotificacion"))) {
            model.addAttribute("notificacion", "Completa tus datos en configuración para continuar.");
            session.removeAttribute("mostrarNotificacion");
        }

        return "inicio";
    }


}
