package Com.Finanzas.FinanzeApp.seguridad;

import io.jsonwebtoken.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.time.Duration;

@Component
public class JwtUtil {

    private final String SECRET_KEY = "JosueClaveSuperSecreta";
    private final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 hora

    // 🔐 Token estándar (1 hora) — usado en login manual
    public String generarToken(String correo) {
        return Jwts.builder()
                .setSubject(correo)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    // 🆕 Token con duración personalizada — usado en login con Google
    public String generateToken(UserDetails userDetails, Duration expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration.toMillis());

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    // ✅ Extraer correo/username
    public String extractUsername(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String extraerCorreo(String token) {
        return extractUsername(token);
    }

    // ✅ Validación simple
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    // ✅ Validación contra username esperado
    public boolean validateToken(String token, String expectedUsername) {
        if (!validateToken(token)) return false;
        String subject = extractUsername(token);
        return subject != null && subject.equals(expectedUsername);
    }

    public boolean validarToken(String token) {
        return validateToken(token);
    }
}
