
package Com.Finanzas.FinanzeApp.servicios.interfaces;

import Com.Finanzas.FinanzeApp.modelos.Usuario;
import Com.Finanzas.FinanzeApp.repositorios.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = (String) attributes.get("email");
        String nombre = (String) attributes.get("name");
        String picture = (String) attributes.get("picture"); // 👈 foto de Google

        Usuario usuario = usuarioRepositorio.findByCorreo(email)
                .orElseGet(() -> {
                    Usuario nuevo = new Usuario();
                    nuevo.setCorreo(email);
                    return nuevo;
                });

        usuario.setNombreCompleto(nombre);
        usuario.setFotoUrl(picture); // 👈 Guardamos la foto de Google
        usuarioRepositorio.save(usuario);

        return oAuth2User;
    }

    private OAuth2User procesarOAuth2User(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        String nombre = oAuth2User.getAttribute("name");
        String proveedorId = oAuth2User.getAttribute("sub");
        String pictureUrl = oAuth2User.getAttribute("picture");

        if (email == null) {
            throw new OAuth2AuthenticationException("No se pudo obtener el correo del usuario de Google");
        }

        // Buscar usuario existente
        Optional<Usuario> usuarioOpt = usuarioRepositorio.findByCorreo(email);
        Usuario usuario = usuarioOpt.orElse(new Usuario());

        usuario.setCorreo(email);
        usuario.setNombreCompleto(nombre);
        usuario.setProveedor("GOOGLE");
        usuario.setProveedorId(proveedorId);

        // Si es nuevo usuario y no tiene foto, guardamos la URL de Google
        if (usuario.getId() == null || usuario.getFotoPerfil() == null) {
            usuario.setFotoPerfil(null); // seguimos mostrando desde la URL
            usuario.setTokenRecuperacion(pictureUrl); // puedes crear un campo exclusivo para URL si prefieres
        }

        // Valores por defecto si es nuevo
        if (usuario.getId() == null) {
            usuario.setContrasena(null);
            usuario.setTema("light");
            usuario.setModoOscuro(false);
            usuario.setMoneda("USD");
        }

        usuarioRepositorio.save(usuario);

        // Retornamos el mismo OAuth2User para que Spring gestione la sesión
        return oAuth2User;
    }
}
