package Com.Finanzas.FinanzeApp.controladores;

import Com.Finanzas.FinanzeApp.modelos.Movimiento;
import Com.Finanzas.FinanzeApp.modelos.Usuario;
import Com.Finanzas.FinanzeApp.modelos.Categoria;
import Com.Finanzas.FinanzeApp.repositorios.CategoriaRepository;
import Com.Finanzas.FinanzeApp.repositorios.UsuarioRepositorio;
import Com.Finanzas.FinanzeApp.servicios.interfaces.MovimientoService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/movimientos")
public class MovimientoController {

    private final MovimientoService movimientoService;
    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepositorio usuarioRepository;

    public MovimientoController(MovimientoService movimientoService,
                                CategoriaRepository categoriaRepository,
                                UsuarioRepositorio usuarioRepository) {
        this.movimientoService = movimientoService;
        this.categoriaRepository = categoriaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    private Usuario getUsuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return usuarioRepository.findByCorreo(auth.getName()).orElse(null);
    }

    @GetMapping
    public String listarMovimientos(Model model) {
        Usuario usuario = getUsuarioAutenticado();
        if (usuario == null) return "redirect:/login";

        List<Movimiento> movimientos = usuario.getMovimientos(); // usando relación en vez del repo

        Double ingresos = movimientoService.obtenerTotalIngresos(usuario.getId());
        Double egresos = movimientoService.obtenerTotalEgresos(usuario.getId());

        model.addAttribute("movimientos", movimientos);
        model.addAttribute("totalIngresos", ingresos);
        model.addAttribute("totalEgresos", egresos);

        return "movimientos";
    }

    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        Usuario usuario = getUsuarioAutenticado();
        if (usuario == null) return "redirect:/login";

        model.addAttribute("movimiento", new Movimiento());
        model.addAttribute("categorias", categoriaRepository.findByUsuarioId(usuario.getId()));
        model.addAttribute("movimientos", usuario.getMovimientos());
        model.addAttribute("modoOscuro", Boolean.TRUE.equals(usuario.getModoOscuro()));

        return "new_movimiento";
    }

    @GetMapping("/api/categoria/{id}/tipo")
    @ResponseBody
    public ResponseEntity<String> obtenerTipoCategoria(@PathVariable Long id) {
        Usuario usuario = getUsuarioAutenticado();
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<Categoria> categoria = categoriaRepository.findById(id);
        if (categoria.isPresent() && categoria.get().getUsuario().getId().equals(usuario.getId())) {
            return ResponseEntity.ok(categoria.get().getTipo());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/nuevo")
    public String guardarMovimiento(@ModelAttribute Movimiento movimiento) {
        Usuario usuario = getUsuarioAutenticado();
        if (usuario == null) return "redirect:/login";

        movimiento.setUsuario(usuario);

        Categoria categoria = categoriaRepository.findById(movimiento.getCategoriaId()).orElse(null);
        if (categoria != null && categoria.getUsuario().getId().equals(usuario.getId())) {
            movimiento.setCategoria(categoria);

            if (movimiento.getTipo() == null || movimiento.getTipo().isEmpty()) {
                movimiento.setTipo(categoria.getTipo());
            }

            // ✅ ahora dispara toda la lógica de metas y notificaciones
            movimientoService.guardarMovimiento(movimiento);

        } else {
            return "redirect:/movimientos/nuevo?error=categoria";
        }

        return "redirect:/inicio";
    }

}
