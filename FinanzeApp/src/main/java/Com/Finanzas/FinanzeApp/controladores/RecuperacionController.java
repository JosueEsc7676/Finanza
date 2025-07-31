package Com.Finanzas.FinanzeApp.controladores;

import Com.Finanzas.FinanzeApp.modelos.Usuario;
import Com.Finanzas.FinanzeApp.repositorios.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/Recuperar")
public class RecuperacionController {


    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @GetMapping
    public String mostrarFormulario() {
        return "Recuperar";
    }

    @PostMapping
    public String procesarRecuperacion(@RequestParam String correo, @RequestParam String nuevaContrasena, Model model) {
        Optional<Usuario> usuarioOpt = usuarioRepositorio.findByCorreo(correo);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            usuario.setContrasena(new BCryptPasswordEncoder().encode(nuevaContrasena));
            usuarioRepositorio.save(usuario);
            model.addAttribute("mensaje", "Contraseña actualizada correctamente");
        } else {
            model.addAttribute("error", "Correo no encontrado");
        }
        return "Recuperar";
    }
}