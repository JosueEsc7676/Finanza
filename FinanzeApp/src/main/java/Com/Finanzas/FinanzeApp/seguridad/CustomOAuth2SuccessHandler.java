//package Com.Finanzas.FinanzeApp.seguridad;
//
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.Cookie;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
//import org.springframework.stereotype.Component;
//
//import Com.Finanzas.FinanzeApp.servicios.interfaces.UsuarioDetailService;
//
//import java.io.IOException;
//import java.time.Duration;
//
//@Component
//public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
//
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    @Autowired
//    private UsuarioDetailService usuarioDetailService;
//
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
//                                        Authentication authentication) throws IOException, ServletException {
//
//        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
//        String email = oauthUser.getAttribute("email");
//
//        if (email != null) {
//            // 🔍 Cargar UserDetails para generar JWT personalizado
//            UserDetails userDetails = usuarioDetailService.loadUserByUsername(email);
//
//            // 🔐 Generar JWT válido por 30 días
//            Duration duracion = Duration.ofDays(30);
//            String token = jwtUtil.generateToken(userDetails, duracion);
//
//            // 🍪 Guardar JWT en cookie persistente
//            Cookie jwtCookie = new Cookie("jwt", token);
//            jwtCookie.setMaxAge((int) duracion.getSeconds());
//            jwtCookie.setHttpOnly(true);
//            jwtCookie.setSecure(false); // ⚠️ solo si estás en localhost sin HTTPS
//            jwtCookie.setPath("/");
//            response.addCookie(jwtCookie);
//
//            System.out.println("🎟️ JWT emitido para usuario Google: " + email);
//        } else {
//            System.out.println("⚠️ No se pudo obtener el email del usuario Google");
//        }
//
//        setDefaultTargetUrl("/inicio");
//        super.onAuthenticationSuccess(request, response, authentication);
//    }
//
//}
package Com.Finanzas.FinanzeApp.seguridad;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import Com.Finanzas.FinanzeApp.modelos.Usuario;
import Com.Finanzas.FinanzeApp.repositorios.UsuarioRepositorio;
import Com.Finanzas.FinanzeApp.servicios.interfaces.UsuarioDetailService;

import java.io.IOException;
import java.time.Duration;
import java.util.Optional;

@Component
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioDetailService usuarioDetailService;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");

        if (email != null) {
            Optional<Usuario> usuarioOpt = usuarioRepositorio.findByCorreo(email);
            Usuario usuario;

            if (usuarioOpt.isEmpty()) {
                // ➕ Usuario nuevo creado por login con Google
                usuario = new Usuario();
                usuario.setCorreo(email);
                usuario.setNombreCompleto(oauthUser.getAttribute("name")); // tu campo es nombreCompleto
                usuario.setDatosCompletos(false); // 👈 marcar que falta completar datos
                usuario.setProveedor("GOOGLE");
                usuario.setProveedorId(oauthUser.getAttribute("sub")); // id único de Google
                usuario.setFotoUrl(oauthUser.getAttribute("picture")); // foto de Google si viene
                usuarioRepositorio.save(usuario);

                System.out.println("🆕 Usuario nuevo creado con Google: " + email);
            } else {
                usuario = usuarioOpt.get();
                // ⚡ No modificar datosCompletos si ya los completó
            }

            // 🔐 Generar JWT con UserDetails
            UserDetails userDetails = usuarioDetailService.loadUserByUsername(email);

            Duration duracion = Duration.ofDays(30);
            String token = jwtUtil.generateToken(userDetails, duracion);

            Cookie jwtCookie = new Cookie("jwt", token);
            jwtCookie.setMaxAge((int) duracion.getSeconds());
            jwtCookie.setHttpOnly(true);
            jwtCookie.setSecure(false); // ⚠️ activar en producción con HTTPS
            jwtCookie.setPath("/");
            response.addCookie(jwtCookie);

            // ⚡ Guardar en sesión si debe ver notificación
            if (!usuario.isDatosCompletos()) {
                request.getSession().setAttribute("mostrarNotificacion", true);
            }

            System.out.println("🎟️ JWT emitido para usuario Google: " + email);
        } else {
            System.out.println("⚠️ No se pudo obtener el email del usuario Google");
        }

        setDefaultTargetUrl("/inicio");
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
