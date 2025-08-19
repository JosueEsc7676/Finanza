package Com.Finanzas.FinanzeApp.controladores;

import Com.Finanzas.FinanzeApp.modelos.Usuario;
import Com.Finanzas.FinanzeApp.repositorios.UsuarioRepositorio;
import Com.Finanzas.FinanzeApp.servicios.interfaces.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.net.InetAddress;


import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/Recuperar")
public class RecuperacionController {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private EmailService emailService;

    @GetMapping
    public String mostrarFormulario() {
        return "Recuperar";
    }

    @PostMapping
    public String procesarRecuperacion(@RequestParam String correo, Model model) {
        Optional<Usuario> usuarioOpt = usuarioRepositorio.findByCorreo(correo);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            String token = UUID.randomUUID().toString();
            usuario.setTokenRecuperacion(token); // Este campo deberías añadirlo a tu entidad
            usuarioRepositorio.save(usuario);

//            String link = "http://192.168.10.140:8080/Recuperar/reset?token=" + token;
//            String link = "https://b644a2cfc04b.ngrok-free.app/Recuperar/reset?token=" + token;
              String link = "https://finanzeapp.pagekite.me/Recuperar/reset?token=" + token;

            try {
                emailService.enviarCorreoRecuperacion(correo, link);
                model.addAttribute("mensaje", "Se ha enviado un enlace a tu correo");
            } catch (Exception e) {
                model.addAttribute("error", "No se pudo enviar el correo: " + e.getMessage());
            }
        } else {
            model.addAttribute("error", "Correo no encontrado");
        }
        return "Recuperar";
    }

    @GetMapping("/reset")
    public String mostrarFormularioCambio(@RequestParam String token, Model model) {
        model.addAttribute("token", token);
        return "CambiarContrasena";
    }

    @PostMapping("/reset")
    public String cambiarContrasena(@RequestParam String token, @RequestParam String nuevaContrasena, Model model) {
        Optional<Usuario> usuarioOpt = usuarioRepositorio.findByTokenRecuperacion(token);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            usuario.setContrasena(new BCryptPasswordEncoder().encode(nuevaContrasena));
            usuario.setTokenRecuperacion(null); // Invalida el token
            usuarioRepositorio.save(usuario);
            model.addAttribute("mensaje", "Contraseña actualizada correctamente");
        } else {
            model.addAttribute("error", "Token inválido o expirado");
        }
        return "CambiarContrasena";
    }
}
