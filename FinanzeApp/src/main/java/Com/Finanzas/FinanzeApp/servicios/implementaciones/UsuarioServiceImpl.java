package Com.Finanzas.FinanzeApp.servicios.implementaciones;

import Com.Finanzas.FinanzeApp.modelos.Usuario;
import Com.Finanzas.FinanzeApp.repositorios.UsuarioRepositorio;
import Com.Finanzas.FinanzeApp.servicios.interfaces.UsuarioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioServicio {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsuarioRepositorio usuarioRepo;

    @Override
    public Usuario registrar(Usuario usuario) {
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
//        usuario.setContrasena(new BCryptPasswordEncoder().encode(usuario.getContrasena()));
        return usuarioRepo.save(usuario);
    }
    @Override
    public Long obtenerIdPorCorreo(String correo) {
        Usuario usuario = usuarioRepo.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con correo: " + correo));
        return usuario.getId();
    }
    @Override
    public Optional<Usuario> buscarPorCorreo(String correo) {
        return usuarioRepo.findByCorreo(correo);
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepo.findById(id);
    }

    @Override
    public Usuario actualizar(Usuario usuario) {
        return usuarioRepo.save(usuario);
    }

    @Override
    public boolean eliminarPorId(Long id) {
        if (usuarioRepo.existsById(id)) {
            usuarioRepo.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public boolean verificarCredenciales(String correo, String contrasena) {
        Optional<Usuario> usuarioOpt = usuarioRepo.findByCorreo(correo);
        if (usuarioOpt.isEmpty()) return false;

        Usuario usuario = usuarioOpt.get();

        // Si el usuario fue creado por Google, no validar contraseña
        if ("GOOGLE".equalsIgnoreCase(usuario.getProveedor())) {
            return true;
        }

        return passwordEncoder.matches(contrasena, usuario.getContrasena());
    }
}
