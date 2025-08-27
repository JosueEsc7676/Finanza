package Com.Finanzas.FinanzeApp.controladores;

import Com.Finanzas.FinanzeApp.modelos.Movimiento;
import Com.Finanzas.FinanzeApp.modelos.Usuario;
import Com.Finanzas.FinanzeApp.modelos.Categoria;
import Com.Finanzas.FinanzeApp.repositorios.CategoriaRepository;
import Com.Finanzas.FinanzeApp.repositorios.UsuarioRepositorio;
import Com.Finanzas.FinanzeApp.servicios.interfaces.MovimientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/movimientos")
public class MovimientoController {

    @Autowired
    private MovimientoService movimientoService;

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

    // 📌 LISTAR
    @GetMapping
    public String listarMovimientos(Model model) {
        Usuario usuario = getUsuarioAutenticado();
        if (usuario == null) return "redirect:/login";

        List<Movimiento> movimientos = usuario.getMovimientos();

        Double ingresos = movimientoService.obtenerTotalIngresos(usuario.getId());
        Double egresos = movimientoService.obtenerTotalEgresos(usuario.getId());

        model.addAttribute("movimientos", movimientos);
        model.addAttribute("totalIngresos", ingresos);
        model.addAttribute("totalEgresos", egresos);

        return "new_movimiento";
    }

    // 📌 FORMULARIO CREAR
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        Usuario usuario = getUsuarioAutenticado();
        if (usuario == null) return "redirect:/login";

        model.addAttribute("movimiento", new Movimiento());
        model.addAttribute("categorias", categoriaRepository.findByUsuarioId(usuario.getId()));
        model.addAttribute("movimientos", usuario.getMovimientos());
        model.addAttribute("modoOscuro", Boolean.TRUE.equals(usuario.getModoOscuro()));
        model.addAttribute("esEdicion", false);

        return "new_movimiento";
    }

    // 📌 GUARDAR NUEVO
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

            movimientoService.guardarMovimiento(movimiento);
        } else {
            return "redirect:/movimientos/nuevo?error=categoria";
        }

        return "redirect:/movimientos/nuevo";
    }

    // 📌 FORMULARIO EDITAR
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model) {
        Usuario usuario = getUsuarioAutenticado();
        if (usuario == null) return "redirect:/login";

        Movimiento movimiento = movimientoService.obtenerPorId(id);
        if (movimiento == null || !movimiento.getUsuario().getId().equals(usuario.getId())) {
            return "redirect:/movimientos/nuevo?error=notfound";
        }

        model.addAttribute("movimiento", movimiento);
        model.addAttribute("categorias", categoriaRepository.findByUsuarioId(usuario.getId()));
        model.addAttribute("movimientos", usuario.getMovimientos());
        model.addAttribute("modoOscuro", Boolean.TRUE.equals(usuario.getModoOscuro()));
        model.addAttribute("esEdicion", true);

        return "new_movimiento";
    }

    // 📌 ACTUALIZAR MOVIMIENTO
    @PostMapping("/editar/{id}")
    public String actualizarMovimiento(@PathVariable Long id, @ModelAttribute Movimiento movimiento) {
        Usuario usuario = getUsuarioAutenticado();
        if (usuario == null) return "redirect:/login";

        Movimiento existente = movimientoService.obtenerPorId(id);
        if (existente == null || !existente.getUsuario().getId().equals(usuario.getId())) {
            return "redirect:/movimientos/nuevo?error=notfound";
        }

        existente.setDescripcion(movimiento.getDescripcion());
        existente.setMonto(movimiento.getMonto());
        existente.setFecha(movimiento.getFecha());

        Categoria categoria = categoriaRepository.findById(movimiento.getCategoriaId()).orElse(null);
        if (categoria != null && categoria.getUsuario().getId().equals(usuario.getId())) {
            existente.setCategoria(categoria);
            existente.setTipo(categoria.getTipo());
        }

        movimientoService.guardarMovimiento(existente);

        return "redirect:/movimientos/nuevo";
    }

    // 📌 ELIMINAR
    @GetMapping("/eliminar/{id}")
    public String eliminarMovimiento(@PathVariable Long id) {
        Usuario usuario = getUsuarioAutenticado();
        if (usuario == null) return "redirect:/login";

        Movimiento movimiento = movimientoService.obtenerPorId(id);
        if (movimiento != null && movimiento.getUsuario().getId().equals(usuario.getId())) {
            movimientoService.eliminarMovimiento(id);
        }

        return "redirect:/movimientos/nuevo";
    }

    // 📌 API para obtener tipo de categoría
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
    // 📌 HISTORIAL
    @GetMapping("/historial")
    public String historial(@RequestParam(required = false) String inicio,
                            @RequestParam(required = false) String fin,
                            Model model) {
        Usuario usuario = getUsuarioAutenticado();
        if (usuario == null) return "redirect:/login";

        List<Movimiento> movimientos;

        if (inicio != null && fin != null) {
            LocalDate fechaInicio = LocalDate.parse(inicio);
            LocalDate fechaFin = LocalDate.parse(fin);
            movimientos = movimientoService.listarPorUsuarioYRango(usuario.getId(), fechaInicio, fechaFin);
        } else {
            movimientos = movimientoService.listarPorUsuario(usuario.getId());
        }

        model.addAttribute("movimientos", movimientos);
        model.addAttribute("inicio", inicio);
        model.addAttribute("fin", fin);
        model.addAttribute("modoOscuro", Boolean.TRUE.equals(usuario.getModoOscuro()));

        return "historial"; // 👈 nombre de tu vista
    }

}
