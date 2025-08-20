//package Com.Finanzas.FinanzeApp.controladores;
//
//import Com.Finanzas.FinanzeApp.modelos.Usuario;
//import Com.Finanzas.FinanzeApp.repositorios.UsuarioRepositorio;
//import Com.Finanzas.FinanzeApp.servicios.interfaces.UsuarioServicio;
//import Com.Finanzas.FinanzeApp.servicios.interfaces.EmailService;
//import jakarta.mail.MessagingException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.security.Principal;
//import java.util.Map;
//import java.util.Optional;
//import java.util.UUID;
//
//@RestController
//@RequestMapping("/usuarios")
//public class UsuarioController {
//
//    @Autowired
//    private UsuarioServicio usuarioServicio;
//    @Autowired
//    private UsuarioRepositorio usuarioRepositorio;
//    @Autowired
//    private EmailService emailService;
//
//    // ---------------------------
//    // CRUD ya existente
//    // ---------------------------
//
//    @GetMapping("/buscar")
//    public ResponseEntity<Usuario> buscarUsuarioPorCorreo(@RequestParam String correo) {
//        Optional<Usuario> usuario = usuarioServicio.buscarPorCorreo(correo);
//        return usuario.map(ResponseEntity::ok)
//                .orElseGet(() -> ResponseEntity.notFound().build());
//    }
//
//    @PostMapping("/usuario/tema")
//    @ResponseBody
//    public ResponseEntity<?> actualizarTema(@RequestBody Map<String, String> datos, Principal principal) {
//        String nuevoTema = datos.get("tema");
//        Optional<Usuario> optionalUsuario = usuarioRepositorio.findByCorreo(principal.getName());
//        if (optionalUsuario.isEmpty()) {
//            return ResponseEntity.status(404).body("Usuario no encontrado");
//        }
//        Usuario usuario = optionalUsuario.get();
//        usuario.setTema(nuevoTema);
//        usuarioRepositorio.save(usuario);
//
//        return ResponseEntity.ok().build();
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<?> actualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuarioActualizado) {
//        Optional<Usuario> usuarioExistente = usuarioServicio.buscarPorId(id);
//        if (usuarioExistente.isEmpty()) {
//            return ResponseEntity.status(404).body("Usuario no encontrado");
//        }
//
//        Usuario usuario = usuarioExistente.get();
//        usuario.setCorreo(usuarioActualizado.getCorreo());
//
//        Usuario actualizado = usuarioServicio.actualizar(usuario);
//        return ResponseEntity.ok(actualizado);
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id) {
//        boolean eliminado = usuarioServicio.eliminarPorId(id);
//        if (eliminado) {
//            return ResponseEntity.ok("Usuario eliminado");
//        } else {
//            return ResponseEntity.status(404).body("Usuario no encontrado");
//        }
//    }
//
//    // ---------------------------
//    // NUEVAS FUNCIONES DE EMAIL
//    // ---------------------------
//
//    // ✅ Recuperación de contraseña
//    @PostMapping("/recuperar")
//    public ResponseEntity<?> enviarCorreoRecuperacion(@RequestParam String correo) {
//        Optional<Usuario> usuario = usuarioRepositorio.findByCorreo(correo);
//        if (usuario.isEmpty()) {
//            return ResponseEntity.status(404).body("Usuario no encontrado");
//        }
//
//        String token = UUID.randomUUID().toString(); // aquí deberías guardarlo en la BD
//        String link = "http://localhost:8080/usuarios/reset?token=" + token;
//
//        try {
//            emailService.enviarCorreoRecuperacion(correo, link);
//            return ResponseEntity.ok("Correo de recuperación enviado a " + correo);
//        } catch (MessagingException e) {
//            return ResponseEntity.status(500).body("Error al enviar correo: " + e.getMessage());
//        }
//    }
//
//    // ✅ Confirmación de cuenta
//    @PostMapping("/confirmacion")
//    public ResponseEntity<?> enviarCorreoConfirmacion(@RequestParam String correo, @RequestParam String nombre) {
//        String link = "http://localhost:8080/usuarios/activar?correo=" + correo;
//        try {
//            emailService.enviarCorreoConfirmacionCuenta(correo, nombre, link);
//            return ResponseEntity.ok("Correo de confirmación enviado a " + correo);
//        } catch (MessagingException e) {
//            return ResponseEntity.status(500).body("Error al enviar correo: " + e.getMessage());
//        }
//    }
//
//    // ✅ Notificación de seguridad
//    @PostMapping("/alerta-seguridad")
//    public ResponseEntity<?> enviarNotificacionSeguridad(@RequestParam String correo, @RequestParam String ip) {
//        try {
//            emailService.enviarNotificacionSeguridad(correo, ip, java.time.LocalDateTime.now().toString());
//            return ResponseEntity.ok("Correo de seguridad enviado a " + correo);
//        } catch (MessagingException e) {
//            return ResponseEntity.status(500).body("Error al enviar correo: " + e.getMessage());
//        }
//    }
//
//    // ✅ Reporte mensual
//    @PostMapping("/reporte-mensual")
//    public ResponseEntity<?> enviarReporteMensual(@RequestParam String correo) {
//        String resumen = "Ingresos: $1200, Gastos: $900, Ahorro: $300"; // ejemplo
//        try {
//            emailService.enviarReporteMensual(correo, resumen);
//            return ResponseEntity.ok("Reporte mensual enviado a " + correo);
//        } catch (MessagingException e) {
//            return ResponseEntity.status(500).body("Error al enviar correo: " + e.getMessage());
//        }
//    }
//
//    // ✅ Aviso de pago próximo
//    @PostMapping("/aviso-pago")
//    public ResponseEntity<?> enviarAvisoPago(@RequestParam String correo,
//                                             @RequestParam String concepto,
//                                             @RequestParam String fecha,
//                                             @RequestParam String monto) {
//        try {
//            emailService.enviarAvisoPagoProximo(correo, concepto, fecha, monto);
//            return ResponseEntity.ok("Aviso de pago enviado a " + correo);
//        } catch (MessagingException e) {
//            return ResponseEntity.status(500).body("Error al enviar correo: " + e.getMessage());
//        }
//    }
//
//    // ✅ Boletín financiero
//    @PostMapping("/boletin")
//    public ResponseEntity<?> enviarBoletin(@RequestParam String correo, @RequestBody String contenido) {
//        try {
//            emailService.enviarBoletinFinanciero(correo, contenido);
//            return ResponseEntity.ok("Boletín enviado a " + correo);
//        } catch (MessagingException e) {
//            return ResponseEntity.status(500).body("Error al enviar correo: " + e.getMessage());
//        }
//    }
//
//}
package Com.Finanzas.FinanzeApp.controladores;

import Com.Finanzas.FinanzeApp.modelos.Usuario;
import Com.Finanzas.FinanzeApp.repositorios.UsuarioRepositorio;
import Com.Finanzas.FinanzeApp.servicios.interfaces.EmailService;
import Com.Finanzas.FinanzeApp.servicios.interfaces.UsuarioServicio;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private EmailService emailService;
    @Autowired
    private UsuarioServicio usuarioServicio;
    private  PersistentTokenRepository tokenRepo;
    // ---------------- LISTAR USUARIOS ----------------
    @GetMapping
    public ResponseEntity<?> listarUsuarios() {
        return ResponseEntity.ok(usuarioRepositorio.findAll());
    }

    // ---------------- OBTENER USUARIO POR ID ----------------
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerUsuario(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioRepositorio.findById(id);
        if (usuario.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(usuario.get());
    }

    // ---------------- CREAR NUEVO USUARIO ----------------
    @PostMapping
    public ResponseEntity<?> crearUsuario(@RequestBody Usuario usuario) {
        return ResponseEntity.ok(usuarioRepositorio.save(usuario));
    }

    // ---------------- ACTUALIZAR USUARIO ----------------
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuarioActualizado) {
        return usuarioRepositorio.findById(id)
                .map(usuario -> {
                    usuario.setCorreo(usuarioActualizado.getCorreo());
                    usuario.setContrasena(usuarioActualizado.getContrasena());
                    usuario.setNombreCompleto(usuarioActualizado.getNombreCompleto());
                    usuario.setTelefono(usuarioActualizado.getTelefono());
                    usuario.setDireccion(usuarioActualizado.getDireccion());
                    usuario.setFechaNacimiento(usuarioActualizado.getFechaNacimiento());
                    usuario.setOcupacion(usuarioActualizado.getOcupacion());
                    usuario.setGenero(usuarioActualizado.getGenero());
                    return ResponseEntity.ok(usuarioRepositorio.save(usuario));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }




    // ---------------- RECUPERAR CONTRASEÑA (ENVÍO DE EMAIL) ----------------
    @PostMapping("/recuperar-contrasena")
    public ResponseEntity<?> recuperarContrasena(@RequestParam String correo) {
        Optional<Usuario> usuario = usuarioRepositorio.findByCorreo(correo);
        if (usuario.isEmpty()) {
            return ResponseEntity.badRequest().body("No existe un usuario con ese correo");
        }
        try {
            String link = "http://localhost:8080/usuarios/resetear-contrasena?correo=" + correo;
            emailService.enviarCorreoRecuperacion(correo, link);
            return ResponseEntity.ok("Correo de recuperación enviado a " + correo);
        } catch (MessagingException e) {
            return ResponseEntity.internalServerError().body("Error al enviar el correo");
        }
    }
    // ---------------- ELIMINAR USUARIO ----------------



}
