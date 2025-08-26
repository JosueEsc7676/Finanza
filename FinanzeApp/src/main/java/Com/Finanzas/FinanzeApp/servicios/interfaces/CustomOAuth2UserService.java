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
        String pictureUrl = (String) attributes.get("picture"); // 👈 foto de Google
        String proveedorId = (String) attributes.get("sub");

        if (email == null) {
            throw new OAuth2AuthenticationException("No se pudo obtener el correo del usuario de Google");
        }

        // Buscar usuario existente
        Usuario usuario = usuarioRepositorio.findByCorreo(email)
                .orElseGet(() -> {
                    Usuario nuevo = new Usuario();
                    nuevo.setCorreo(email);
                    return nuevo;
                });

        // Actualizamos datos siempre
        usuario.setNombreCompleto(nombre);
        usuario.setProveedor("GOOGLE");
        usuario.setProveedorId(proveedorId);

        // Siempre usamos fotoUrl para guardar la foto de Google
        usuario.setFotoUrl(pictureUrl);

        // Valores por defecto si es nuevo
        if (usuario.getId() == null) {
            usuario.setContrasena("{noop}GOOGLE");
            usuario.setTema("light");
            usuario.setModoOscuro(false);
            usuario.setMoneda("USD");
        }

        usuarioRepositorio.save(usuario);

        return oAuth2User;
    }
}
