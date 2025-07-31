package Com.Finanzas.FinanzeApp.controladores;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
@RequestMapping ("/")

@Controller
public class HomeController {

    @GetMapping("/inicio")
    public String mostrarInicio() {
        return "inicio"; // archivo inicio.html en /templates
    }

    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }
}


