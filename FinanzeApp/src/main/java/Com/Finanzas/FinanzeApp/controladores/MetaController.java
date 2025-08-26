package Com.Finanzas.FinanzeApp.controladores;

import Com.Finanzas.FinanzeApp.modelos.Meta;
import Com.Finanzas.FinanzeApp.modelos.Movimiento;
import Com.Finanzas.FinanzeApp.modelos.Usuario;
import Com.Finanzas.FinanzeApp.repositorios.MovimientoRepository;
import Com.Finanzas.FinanzeApp.repositorios.UsuarioRepositorio;
import Com.Finanzas.FinanzeApp.servicios.interfaces.MetaService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/metas")
public class MetaController {

    @Autowired
    private MetaService metaServicio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private MovimientoRepository movimientoRepositorio;

    // 🔹 Listar todas las metas del usuario logueado
    @GetMapping
    public String listarMetas(Model model, Principal principal) {
        String correo = principal.getName();
        Usuario usuario = usuarioRepositorio.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Meta> metas = metaServicio.obtenerPorUsuario(usuario);

        model.addAttribute("metas", metas);
        model.addAttribute("nuevaMeta", new Meta());
        return "metas"; // vista: metas/lista.html
    }

    // 🔹 Guardar nueva meta
    @PostMapping("/guardar")
    public String guardarMeta(@ModelAttribute("nuevaMeta") Meta meta, Principal principal) {
        String correo = principal.getName();
        Usuario usuario = usuarioRepositorio.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        meta.setUsuario(usuario);
        metaServicio.guardarMeta(meta);
        return "redirect:/metas";
    }

    // 🔹 Editar meta
    @GetMapping("/editar/{id}")
    public String editarMeta(@PathVariable Long id, Model model, Principal principal) {
        Meta meta = metaServicio.obtenerPorId(id)
                .orElseThrow(() -> new RuntimeException("Meta no encontrada"));

        // Validar que la meta pertenezca al usuario logueado
        String correo = principal.getName();
        Usuario usuario = usuarioRepositorio.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        if (!meta.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("No tienes permiso para editar esta meta");
        }

        model.addAttribute("meta", meta);
        return "metas/editar"; // vista: metas/editar.html
    }

    @PostMapping("/actualizar/{id}")
    public String actualizarMeta(@PathVariable Long id,
                                 @ModelAttribute("meta") Meta metaActualizada,
                                 Principal principal) {
        Meta meta = metaServicio.obtenerPorId(id)
                .orElseThrow(() -> new RuntimeException("Meta no encontrada"));

        // Validar que la meta pertenezca al usuario logueado
        String correo = principal.getName();
        Usuario usuario = usuarioRepositorio.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        if (!meta.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("No tienes permiso para actualizar esta meta");
        }

        meta.setTitulo(metaActualizada.getTitulo());
        meta.setMontoMeta(metaActualizada.getMontoMeta());
        meta.setFechaLimite(metaActualizada.getFechaLimite());
        meta.setEstado(metaActualizada.getEstado());

        metaServicio.guardarMeta(meta);
        return "redirect:/metas";
    }

    // 🔹 Eliminar meta
    @GetMapping("/eliminar/{id}")
    public String eliminarMeta(@PathVariable Long id, Principal principal) {
        Meta meta = metaServicio.obtenerPorId(id)
                .orElseThrow(() -> new RuntimeException("Meta no encontrada"));

        String correo = principal.getName();
        Usuario usuario = usuarioRepositorio.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        if (!meta.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("No tienes permiso para eliminar esta meta");
        }

        metaServicio.eliminarMeta(id);
        return "redirect:/metas";
    }

    // 🔹 Ver detalle de una meta con sus movimientos
    @GetMapping("/{id}/detalles")
    public String verDetallesMeta(@PathVariable Long id, Model model, Principal principal) {
        String correo = principal.getName();
        Usuario usuario = usuarioRepositorio.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Meta meta = metaServicio.obtenerPorId(id)
                .orElseThrow(() -> new RuntimeException("Meta no encontrada"));

        if (!meta.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("No tienes permiso para ver esta meta");
        }

        double progreso = (meta.getMontoActual() / meta.getMontoMeta()) * 100;

        model.addAttribute("meta", meta);
        model.addAttribute("movimientos", meta.getMovimientos());
        model.addAttribute("notificaciones", meta.getNotificaciones());
        model.addAttribute("progreso", progreso);

        return "metas/detalle"; // vista: metas/detalle.html
    }

    // 🔹 Agregar un movimiento directamente a una meta
    @PostMapping("/{id}/agregar-movimiento")
    public String agregarMovimiento(@PathVariable Long id,
                                    @RequestParam double monto,
                                    @RequestParam String descripcion,
                                    Principal principal) {
        String correo = principal.getName();
        Usuario usuario = usuarioRepositorio.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Meta meta = metaServicio.obtenerPorId(id)
                .orElseThrow(() -> new RuntimeException("Meta no encontrada"));

        if (!meta.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("No tienes permiso para modificar esta meta");
        }

        Movimiento movimiento = new Movimiento();
        movimiento.setMonto(monto);
        movimiento.setDescripcion(descripcion);
        movimiento.setFecha(LocalDate.now());
        movimiento.setTipo("Ingreso"); // aporte hacia la meta
        movimiento.setUsuario(usuario);
        movimiento.setMeta(meta);

        movimientoRepositorio.save(movimiento);

        meta.agregarMovimiento(movimiento);
        metaServicio.guardarMeta(meta);

        return "redirect:/metas/" + id + "/detalles";
    }
    @PostMapping("/{id}/completar")
    public String completarMeta(@PathVariable Long id, Principal principal) {
        String correo = principal.getName();
        Usuario usuario = usuarioRepositorio.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        try {
            metaServicio.completarMeta(id, usuario);
        } catch (MessagingException e) {
            e.printStackTrace(); // o loguear con logger
        }

        return "redirect:/metas/" + id + "/detalles";
    }

}
