package Com.Finanzas.FinanzeApp.controladores;

import Com.Finanzas.FinanzeApp.modelos.Notificacion;
import Com.Finanzas.FinanzeApp.modelos.Usuario;
import Com.Finanzas.FinanzeApp.repositorios.NotificacionRepositorio;
import Com.Finanzas.FinanzeApp.repositorios.UsuarioRepositorio;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/notificaciones")
public class NotificacionController {

    private final NotificacionRepositorio notificacionRepo;
    private final UsuarioRepositorio usuarioRepo;

    // ✅ Inyección por constructor
    public NotificacionController(NotificacionRepositorio notificacionRepo, UsuarioRepositorio usuarioRepo) {
        this.notificacionRepo = notificacionRepo;
        this.usuarioRepo = usuarioRepo;
    }

    // Listado con orden dinámico (asc/desc)
    @GetMapping
    public String listarNotificaciones(@RequestParam(name = "orden", defaultValue = "desc") String orden,
                                       Model model,
                                       Principal principal) {
        if (principal == null) return "redirect:/login";

        Usuario usuario = usuarioRepo.findByCorreo(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Sort sort = "asc".equalsIgnoreCase(orden)
                ? Sort.by("fechaEnvio").ascending()
                : Sort.by("fechaEnvio").descending();

        // 👇 Traer notificaciones primero
        List<Notificacion> notificaciones = notificacionRepo.findAllByUsuario(usuario, sort);

        // 👇 Marcar como leídas
        notificaciones.forEach(n -> {
            if (!Boolean.TRUE.equals(n.getLeida())) {
                n.setLeida(true);
                notificacionRepo.save(n);
            }
        });

        // 👇 Consultar si todavía hay NO leídas
        boolean tieneNoLeidas = !notificacionRepo.findByUsuarioAndLeidaFalse(usuario).isEmpty();

        model.addAttribute("notificaciones", notificaciones);
        model.addAttribute("orden", orden);
        model.addAttribute("tieneNotificaciones", tieneNoLeidas);

        return "notificaciones";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarNotificacion(@PathVariable Long id, Principal principal) {
        String correo = principal.getName();
        Optional<Usuario> usuarioOpt = usuarioRepo.findByCorreo(correo);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            notificacionRepo.findById(id).ifPresent(notificacion -> {
                if (notificacion.getUsuario().getId().equals(usuario.getId())) {
                    notificacionRepo.delete(notificacion);
                }
            });
        }

        return "redirect:/notificaciones";
    }

    @PostMapping("/eliminar-todas")
    public String eliminarTodasNotificaciones(Principal principal) {
        String correo = principal.getName();
        Optional<Usuario> usuarioOpt = usuarioRepo.findByCorreo(correo);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            notificacionRepo.deleteAllByUsuario(usuario);
        }

        return "redirect:/notificaciones";
    }
}
