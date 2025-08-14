package Com.Finanzas.FinanzeApp.controladores;

import Com.Finanzas.FinanzeApp.modelos.Usuario;
import Com.Finanzas.FinanzeApp.repositorios.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/Registro")  // importante usar minúsculas por convención
public class RegistroController {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @GetMapping
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "Registro"; // archivo HTML en src/main/resources/templates/registro.html
    }

    @PostMapping
    public String registrarUsuario(@ModelAttribute("usuario") Usuario usuario, Model model) {
        if (usuarioRepositorio.findByCorreo(usuario.getCorreo()).isPresent()) {
            model.addAttribute("error", "El correo ya está registrado.");
            return "Registro";
        }

        // Encriptar la contraseña antes de guardar
        usuario.setContrasena(new BCryptPasswordEncoder().encode(usuario.getContrasena()));
        usuarioRepositorio.save(usuario);

        return "redirect:/login?registroExitoso";
    }
}
