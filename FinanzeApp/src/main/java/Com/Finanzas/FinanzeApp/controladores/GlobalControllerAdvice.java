package Com.Finanzas.FinanzeApp.controladores;

import Com.Finanzas.FinanzeApp.modelos.Usuario;
import Com.Finanzas.FinanzeApp.repositorios.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.security.core.Authentication;

import java.util.Base64;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    @ModelAttribute("fotoPerfilNavbar")
    public String fotoPerfil(Authentication authentication) {
        if (authentication == null) return "/images/default-avatar.png";

        String correo = authentication.getName();
        Usuario usuario = usuarioRepositorio.findByCorreo(correo).orElse(null);

        if (usuario != null) {
            if ("GOOGLE".equals(usuario.getProveedor())) {
                return usuario.getFotoUrl();
            } else if (usuario.getFotoPerfil() != null) {
                return "data:image/png;base64," + Base64.getEncoder().encodeToString(usuario.getFotoPerfil());
            }
        }
        return "/images/default-avatar.png";
    }

}
