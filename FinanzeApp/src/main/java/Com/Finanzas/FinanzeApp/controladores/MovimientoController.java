package Com.Finanzas.FinanzeApp.controladores;

import Com.Finanzas.FinanzeApp.modelos.Movimiento;
import Com.Finanzas.FinanzeApp.modelos.Usuario;
import Com.Finanzas.FinanzeApp.modelos.Categoria;
import Com.Finanzas.FinanzeApp.repositorios.MovimientoRepository;
import Com.Finanzas.FinanzeApp.repositorios.CategoriaRepository;
import Com.Finanzas.FinanzeApp.repositorios.UsuarioRepositorio;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/movimientos")
public class MovimientoController {

    private final MovimientoRepository movimientoRepository;
    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepositorio usuarioRepository;

    public MovimientoController(MovimientoRepository movimientoRepository,
                                CategoriaRepository categoriaRepository,
                                UsuarioRepositorio usuarioRepository) {
        this.movimientoRepository = movimientoRepository;
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

        List<Movimiento> movimientos = movimientoRepository.findByUsuarioId(usuario.getId());

        // Repo actualizado devuelve Double
        Double ingresos = movimientoRepository.totalIngresos(usuario.getId());
        Double egresos = movimientoRepository.totalEgresos(usuario.getId());

        // Por seguridad ante posibles null en implementaciones antiguas
        if (ingresos == null) ingresos = 0.0;
        if (egresos == null) egresos = 0.0;

        model.addAttribute("movimientos", movimientos);
        model.addAttribute("totalIngresos", ingresos); // En Thymeleaf puedes formatear con #numbers
        model.addAttribute("totalEgresos", egresos);

        return "movimientos";
    }

    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        Usuario usuario = getUsuarioAutenticado();
        if (usuario == null) return "redirect:/login";

        model.addAttribute("movimiento", new Movimiento());
        model.addAttribute("categorias", categoriaRepository.findByUsuarioId(usuario.getId()));
        model.addAttribute("movimientos", movimientoRepository.findByUsuarioId(usuario.getId()));
        model.addAttribute("modoOscuro", Boolean.TRUE.equals(usuario.getModoOscuro()));

        return "new_movimiento";
    }

    @PostMapping("/nuevo")
    public String guardarMovimiento(@ModelAttribute Movimiento movimiento) {
        Usuario usuario = getUsuarioAutenticado();
        if (usuario == null) return "redirect:/login";

        movimiento.setUsuario(usuario);

        Categoria categoria = categoriaRepository.findById(movimiento.getCategoriaId()).orElse(null);
        if (categoria != null && categoria.getUsuario().getId().equals(usuario.getId())) {
            movimiento.setCategoria(categoria);
        } else {
            return "redirect:/movimientos/nuevo?error=categoria";
        }

        movimientoRepository.save(movimiento);
        return "redirect:/inicio";
    }

    @PostMapping("/editar/{id}")
    public String editarMovimiento(@PathVariable Long id, @ModelAttribute Movimiento movimientoEditado) {
        Usuario usuario = getUsuarioAutenticado();
        if (usuario == null) return "redirect:/login";

        Movimiento movimientoExistente = movimientoRepository.findById(id).orElse(null);
        if (movimientoExistente == null || !movimientoExistente.getUsuario().getId().equals(usuario.getId())) {
            return "redirect:/movimientos/nuevo?error=acceso";
        }

        // Verificar que la categoría pertenece al usuario
        Categoria categoria = categoriaRepository.findById(movimientoEditado.getCategoriaId()).orElse(null);
        if (categoria != null && categoria.getUsuario().getId().equals(usuario.getId())) {
            movimientoExistente.setTipo(movimientoEditado.getTipo());
            movimientoExistente.setCategoria(categoria);
            movimientoExistente.setCategoriaId(movimientoEditado.getCategoriaId());
            movimientoExistente.setMonto(movimientoEditado.getMonto());
            movimientoExistente.setFecha(movimientoEditado.getFecha());
            movimientoExistente.setDescripcion(movimientoEditado.getDescripcion());

            movimientoRepository.save(movimientoExistente);
        } else {
            return "redirect:/movimientos/nuevo?error=categoria";
        }

        return "redirect:/movimientos/nuevo";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarMovimiento(@PathVariable Long id) {
        Usuario usuario = getUsuarioAutenticado();
        if (usuario == null) return "redirect:/login";

        Movimiento movimiento = movimientoRepository.findById(id).orElse(null);
        if (movimiento != null && movimiento.getUsuario().getId().equals(usuario.getId())) {
            movimientoRepository.delete(movimiento);
        }

        return "redirect:/movimientos/nuevo";
    }
}
