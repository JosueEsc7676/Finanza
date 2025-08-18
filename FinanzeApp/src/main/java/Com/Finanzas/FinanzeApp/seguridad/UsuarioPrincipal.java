package Com.Finanzas.FinanzeApp.seguridad;

import Com.Finanzas.FinanzeApp.modelos.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class UsuarioPrincipal implements OAuth2User, UserDetails {

    private Usuario usuario;
    private Map<String, Object> atributos;

    public UsuarioPrincipal(Usuario usuario, Map<String, Object> atributos) {
        this.usuario = usuario;
        this.atributos = atributos;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return atributos;
    }

    @Override
    public String getName() {
        return usuario.getCorreo();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }


    @Override
    public String getPassword() {
        return usuario.getContrasena();
    }

    @Override
    public String getUsername() {
        return usuario.getCorreo();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
