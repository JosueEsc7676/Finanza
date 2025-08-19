package Com.Finanzas.FinanzeApp.controladores;

import Com.Finanzas.FinanzeApp.modelos.Movimiento;
import Com.Finanzas.FinanzeApp.modelos.Usuario;
import Com.Finanzas.FinanzeApp.repositorios.MovimientoRepository;
import Com.Finanzas.FinanzeApp.repositorios.UsuarioRepositorio;
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
        return "Login";
    }

//    @GetMapping("/inicio")
//    public String mostrarInicio(Model model) {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        String correo = auth.getName();
//
//        Usuario usuario = usuarioRepositorio.findByCorreo(correo).orElse(null);
//
//        if (usuario != null) {
//            model.addAttribute("usuario", usuario);
//
//            Double totalIngresos = movimientoRepository.totalIngresos(usuario.getId());
//            Double totalEgresos = movimientoRepository.totalEgresos(usuario.getId());
//
//            totalIngresos = totalIngresos != null ? totalIngresos : 0.0;
//            totalEgresos = totalEgresos != null ? totalEgresos : 0.0;
//            double balance = totalIngresos - totalEgresos;
//
//            model.addAttribute("totalIngresos", totalIngresos);
//            model.addAttribute("totalEgresos", totalEgresos);
//            model.addAttribute("balance", balance);
//
//            // Agregar los movimientos del usuario al modelo
//            List<Movimiento> movimientosRecientes = movimientoRepository
//                    .findTop4ByUsuarioIdOrderByFechaDesc(usuario.getId());
//            model.addAttribute("movimientos", movimientosRecientes);
//
//            // ✅ Se pasa el valor al layout
//            model.addAttribute("modoOscuro", Boolean.TRUE.equals(usuario.getModoOscuro()));
//        } else {
//            return "redirect:/login";
//        }
//
//        return "inicio";
//    }

//    @GetMapping("/inicio")
//    public String mostrarInicio(Model model, Authentication authentication) {
//        String correo = null;
//
//        if (authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
//            // 🟢 Usuario de tu BD (login con correo y contraseña)
//            correo = authentication.getName();
//
//        } else if (authentication instanceof OAuth2AuthenticationToken) {
//            // 🔵 Usuario de Google
//            var attributes = ((OAuth2AuthenticationToken) authentication).getPrincipal().getAttributes();
//            correo = (String) attributes.get("email");
//
//            // 👉 Crear usuario en BD si no existe (sin usar lambda)
//            Usuario usuarioExistente = usuarioRepositorio.findByCorreo(correo).orElse(null);
//            if (usuarioExistente == null) {
//                Usuario nuevo = new Usuario();
//                nuevo.setCorreo(correo);
//                nuevo.setNombreCompleto((String) attributes.get("name")); // usa tu campo nombreCompleto
//                nuevo.setFotoPerfil(null); // más adelante puedes mapear la foto de Google aquí
//                usuarioRepositorio.save(nuevo);
//            }
//        }
//
//        // --- lógica principal ---
//        Usuario usuario = usuarioRepositorio.findByCorreo(correo).orElse(null);
//        if (usuario == null) {
//            return "redirect:/login";
//        }
//
//        model.addAttribute("usuario", usuario);
//
//        Double totalIngresos = movimientoRepository.totalIngresos(usuario.getId());
//        Double totalEgresos = movimientoRepository.totalEgresos(usuario.getId());
//
//        totalIngresos = totalIngresos != null ? totalIngresos : 0.0;
//        totalEgresos = totalEgresos != null ? totalEgresos : 0.0;
//        double balance = totalIngresos - totalEgresos;
//
//        model.addAttribute("totalIngresos", totalIngresos);
//        model.addAttribute("totalEgresos", totalEgresos);
//        model.addAttribute("balance", balance);
//
//        model.addAttribute("movimientos",
//                movimientoRepository.findTop4ByUsuarioIdOrderByFechaDesc(usuario.getId()));
//
//        model.addAttribute("modoOscuro", Boolean.TRUE.equals(usuario.getModoOscuro()));
//
//        return "inicio";
//    }

    @GetMapping("/inicio")
    public String mostrarInicio(Model model,
                                @AuthenticationPrincipal OAuth2User oauthUser,
                                Authentication authentication) {
        String correo = null;

        if (oauthUser != null) {
            correo = oauthUser.getAttribute("email");
            var usuarioExistente = usuarioRepositorio.findByCorreo(correo).orElse(null);
            if (usuarioExistente == null) {
                Usuario nuevo = new Usuario();
                nuevo.setCorreo(correo);
                nuevo.setNombreCompleto(oauthUser.getAttribute("name"));
                nuevo.setContrasena("{noop}GOOGLE");
                nuevo.setProveedor("GOOGLE");
                nuevo.setProveedorId(oauthUser.getAttribute("sub"));
                nuevo.setFotoUrl(oauthUser.getAttribute("picture")); // 👈 Guardar URL de foto
                usuarioRepositorio.save(nuevo);
            }
        } else {
            correo = authentication.getName(); // login normal
        }

        if (correo == null) {
            return "redirect:/login?error=true";
        }

        Usuario usuario = usuarioRepositorio.findByCorreo(correo).orElse(null);
        if (usuario == null) {
            return "redirect:/login";
        }

        model.addAttribute("usuario", usuario);

        // 👇 Aquí decides qué foto mandar al frontend
        if ("GOOGLE".equals(usuario.getProveedor()) && usuario.getFotoUrl() != null) {
            model.addAttribute("fotoGoogle", usuario.getFotoUrl());
        } else if (usuario.getFotoPerfil() != null) {
            model.addAttribute("fotoBase64",
                    Base64.getEncoder().encodeToString(usuario.getFotoPerfil()));
        }

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

        return "inicio";
    }

}
