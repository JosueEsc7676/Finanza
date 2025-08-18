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

import Com.Finanzas.FinanzeApp.servicios.interfaces.UsuarioDetailService;

import java.io.IOException;
import java.time.Duration;

@Component
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioDetailService usuarioDetailService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");

        if (email != null) {
            // 🔍 Cargar UserDetails para generar JWT personalizado
            UserDetails userDetails = usuarioDetailService.loadUserByUsername(email);

            // 🔐 Generar JWT válido por 30 días
            Duration duracion = Duration.ofDays(30);
            String token = jwtUtil.generateToken(userDetails, duracion);

            // 🍪 Guardar JWT en cookie persistente
            Cookie jwtCookie = new Cookie("jwt", token);
            jwtCookie.setMaxAge((int) duracion.getSeconds());
            jwtCookie.setHttpOnly(true);
            jwtCookie.setSecure(false); // ⚠️ solo si estás en localhost sin HTTPS
            jwtCookie.setPath("/");
            response.addCookie(jwtCookie);

            System.out.println("🎟️ JWT emitido para usuario Google: " + email);
        } else {
            System.out.println("⚠️ No se pudo obtener el email del usuario Google");
        }

        setDefaultTargetUrl("/inicio");
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
