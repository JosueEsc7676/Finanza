package Com.Finanzas.FinanzeApp.controladores;

import Com.Finanzas.FinanzeApp.modelos.Usuario;
import Com.Finanzas.FinanzeApp.servicios.interfaces.UsuarioServicio;
import Com.Finanzas.FinanzeApp.repositorios.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioServicio usuarioServicio;
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @GetMapping("/buscar")
    public ResponseEntity<Usuario> buscarUsuarioPorCorreo(@RequestParam String correo) {
        Optional<Usuario> usuario = usuarioServicio.buscarPorCorreo(correo);
        return usuario.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/usuario/tema")
    @ResponseBody
    public ResponseEntity<?> actualizarTema(@RequestBody Map<String, String> datos, Principal principal) {
        String nuevoTema = datos.get("tema");
        Optional<Usuario> optionalUsuario = usuarioRepositorio.findByCorreo(principal.getName());
        if (optionalUsuario.isEmpty()) {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }
        Usuario usuario = optionalUsuario.get();
        usuario.setTema(nuevoTema);
        usuarioRepositorio.save(usuario);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuarioActualizado) {
        Optional<Usuario> usuarioExistente = usuarioServicio.buscarPorId(id);
        if (usuarioExistente.isEmpty()) {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }

        Usuario usuario = usuarioExistente.get();

        usuario.setCorreo(usuarioActualizado.getCorreo());

        Usuario actualizado = usuarioServicio.actualizar(usuario);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id) {
        boolean eliminado = usuarioServicio.eliminarPorId(id);
        if (eliminado) {
            return ResponseEntity.ok("Usuario eliminado");
        } else {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }
    }
}
