package Com.Finanzas.FinanzeApp.controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import Com.Finanzas.FinanzeApp.modelos.Usuario;
import Com.Finanzas.FinanzeApp.seguridad.JwtUtil;
import Com.Finanzas.FinanzeApp.servicios.interfaces.UsuarioServicio;

import java.util.HashMap;
import java.util.Map;
@Controller
@RestController
@RequestMapping("/auth")
public class AuthControlador {

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/Register")
    public ResponseEntity<?> registrarUsuario(@RequestBody Usuario usuario) {
        if (usuario.getCorreo() == null || usuario.getContrasena() == null || usuario.getNombreCompleto() == null){
            return ResponseEntity.badRequest().body("Faltan datos");
        }

        if (usuarioServicio.buscarPorCorreo(usuario.getCorreo()).isPresent()) {
            return ResponseEntity.badRequest().body("Correo ya registrado");
        }

        Usuario nuevo = usuarioServicio.registrar(usuario);
        return ResponseEntity.ok(nuevo);
    }


//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody Usuario usuario) {
//        boolean credencialesValidas = usuarioServicio.verificarCredenciales(usuario.getCorreo(), usuario.getContrasena());
//
//        if (!credencialesValidas) {
//            return ResponseEntity.status(401).body("Credenciales incorrectas");
//        }
//
//        String token = jwtUtil.generarToken(usuario.getCorreo());
//        Map<String, String> respuesta = new HashMap<>();
//        respuesta.put("token", token);
//        return ResponseEntity.ok(respuesta);
//    }
@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody Usuario usuario) {
    boolean credencialesValidas = usuarioServicio.verificarCredenciales(usuario.getCorreo(), usuario.getContrasena());

    if (!credencialesValidas) {
        return ResponseEntity.status(401).body("Credenciales incorrectas");
    }

    String token = jwtUtil.generarToken(usuario.getCorreo());
    Map<String, String> respuesta = new HashMap<>();
    respuesta.put("token", token);
    return ResponseEntity.ok(respuesta);
}

}
