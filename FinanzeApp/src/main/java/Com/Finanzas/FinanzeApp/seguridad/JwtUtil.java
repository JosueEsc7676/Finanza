package Com.Finanzas.FinanzeApp.seguridad;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    private final String SECRET_KEY = "JosueClaveSuperSecreta";
    private final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 hora

    public String generarToken(String correo) {
        return Jwts.builder()
                .setSubject(correo)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    // Método con nombre en inglés que espera el filtro (equivalente a extraerCorreo)
    public String extractUsername(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Validación simple del token
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    // Si quieres validar también contra el username del UserDetails:
    public boolean validateToken(String token, String expectedUsername) {
        if (!validateToken(token)) return false;
        String subject = extractUsername(token);
        return subject != null && subject.equals(expectedUsername);
    }

    // Conserva tus nombres anteriores si quieres compatibilidad
    public String extraerCorreo(String token) {
        return extractUsername(token);
    }

    public boolean validarToken(String token) {
        return validateToken(token);
    }
}
