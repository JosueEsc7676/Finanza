//package Com.Finanzas.FinanzeApp.controladores;
//
//import Com.Finanzas.FinanzeApp.servicios.interfaces.UsuarioServicio;
//import jakarta.servlet.http.Cookie;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
//import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//@Controller
//@RequestMapping("/user")
//@RequiredArgsConstructor
//public class UsuarioMvcController {
//
//    private final UsuarioServicio usuarioServicio;
//    private final PersistentTokenRepository tokenRepo; // si usas remember-me persistente
//
//    // GET → Mostrar confirmación
//    @GetMapping("/eliminar")
//    public String mostrarConfirmacionEliminar() {
//        return "eliminar-cuenta"; // nombre de la plantilla en /templates
//    }
//
//    // POST → Eliminar usuario
//    @PostMapping("/eliminar")
//    public String eliminarCuenta(Authentication auth,
//                                 HttpServletRequest request,
//                                 HttpServletResponse response) {
//        String correo = auth.getName();
//        Long usuarioId = usuarioServicio.obtenerIdPorCorreo(correo);
//
//        boolean eliminado = usuarioServicio.eliminarPorId(usuarioId);
//        if (!eliminado) {
//            return "redirect:/configuracion?error";
//        }
//
//        // 1) Revocar tokens remember-me en BD (si aplica)
//        if (tokenRepo != null) {
//            tokenRepo.removeUserTokens(correo);
//        }
//
//        // 2) Cerrar sesión
//        new SecurityContextLogoutHandler().logout(request, response, auth);
//
//        // 3) Borrar cookies
//        deleteCookie("jwt", response);
//        deleteCookie("remember-me", response);
//        deleteCookie("JSESSIONID", response);
//
//        // 4) Limpiar storage en front
//        response.setHeader("Clear-Site-Data", "\"cookies\", \"storage\"");
//
//        // 5) Redirigir
//        return "redirect:/login?eliminado";
//    }
//
//    private void deleteCookie(String name, HttpServletResponse res) {
//        Cookie c = new Cookie(name, null);
//        c.setPath("/");
//        c.setMaxAge(0);
//        c.setHttpOnly(true);
//        c.setSecure(true); // Activar solo con HTTPS
//        res.addCookie(c);
//    }
//}
