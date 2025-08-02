package Com.Finanzas.FinanzeApp.controladores;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/")
    public String redirigirLogin() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String mostrarLogin() {
        return "Login"; // Nombre del archivo Login.html o .jsp
    }

    @GetMapping("/inicio")
    public String mostrarInicio() {
        return "Inicio"; // Página principal protegida
    }
}
