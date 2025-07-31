package Com.Finanzas.FinanzeApp.controladores;

import Com.Finanzas.FinanzeApp.modelos.Usuario;
import Com.Finanzas.FinanzeApp.repositorios.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/Registro")
public class RegistroController {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @GetMapping
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "Registro";
    }


    @PostMapping
    public String registrarUsuario(@ModelAttribute("usuario") Usuario usuario, Model model) {
        // Validar correo duplicado
        if (usuarioRepositorio.findByCorreo(usuario.getCorreo()).isPresent()) {
            model.addAttribute("error", "El correo ya está registrado.");
            return "Registro";
        }

        // Encriptar contraseña
        usuario.setContrasena(new BCryptPasswordEncoder().encode(usuario.getContrasena()));

        // Guardar en base de datos
        usuarioRepositorio.save(usuario);

        // Redirigir al login
        return "redirect:/login";
    }
}
