
package Com.Finanzas.FinanzeApp.controladores;

import Com.Finanzas.FinanzeApp.modelos.Usuario;
import Com.Finanzas.FinanzeApp.seguridad.JwtUtil;
import Com.Finanzas.FinanzeApp.servicios.interfaces.UsuarioServicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/auth")
public class AuthControlador {

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/Register")
    public ResponseEntity<?> registrarUsuario(@RequestBody Usuario usuario) {
        if (usuario.getCorreo() == null || usuario.getContrasena() == null || usuario.getNombreCompleto() == null) {
            return ResponseEntity.badRequest().body("Faltan datos");
        }

        if (usuarioServicio.buscarPorCorreo(usuario.getCorreo()).isPresent()) {
            return ResponseEntity.badRequest().body("Correo ya registrado");
        }

        // 🔐 Cifrar la contraseña antes de guardar
        String hashed = passwordEncoder.encode(usuario.getContrasena());
        usuario.setContrasena(hashed);

        Usuario nuevo = usuarioServicio.registrar(usuario);
        return ResponseEntity.ok(nuevo);
    }
}
