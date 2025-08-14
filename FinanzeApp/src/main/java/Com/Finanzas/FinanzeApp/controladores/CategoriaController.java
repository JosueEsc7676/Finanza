package Com.Finanzas.FinanzeApp.controladores;

import Com.Finanzas.FinanzeApp.modelos.Categoria;
import Com.Finanzas.FinanzeApp.modelos.Usuario;
import Com.Finanzas.FinanzeApp.repositorios.CategoriaRepository;
import Com.Finanzas.FinanzeApp.repositorios.UsuarioRepositorio;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepositorio usuarioRepositorio;

    public CategoriaController(CategoriaRepository categoriaRepository, UsuarioRepositorio usuarioRepositorio) {
        this.categoriaRepository = categoriaRepository;
        this.usuarioRepositorio = usuarioRepositorio;
    }

    private Usuario getUsuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return usuarioRepositorio.findByCorreo(auth.getName()).orElse(null);
    }

    // LISTAR
    @GetMapping
    public String listarCategorias(@RequestParam(name = "edit", required = false) Long editId, Model model) {
        Usuario usuario = getUsuarioAutenticado();
        if (usuario == null) return "redirect:/login";

        model.addAttribute("categorias", categoriaRepository.findByUsuarioId(usuario.getId()));

        Categoria categoria = new Categoria(); // default (crear)
        if (editId != null) {
            Optional<Categoria> editable = categoriaRepository.findById(editId);
            if (editable.isPresent() && editable.get().getUsuario().getId().equals(usuario.getId())) {
                categoria = editable.get();
            }
        }

        model.addAttribute("categoria", categoria);
        return "categorias";
    }

    // GUARDAR NUEVA
    @PostMapping("/nueva")
    public String guardarCategoria(@ModelAttribute Categoria categoria) {
        Usuario usuario = getUsuarioAutenticado();
        if (usuario == null) return "redirect:/login";

        categoria.setUsuario(usuario);
        categoriaRepository.save(categoria);
        return "redirect:/categorias";
    }

    // FORMULARIO EDITAR
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Usuario usuario = getUsuarioAutenticado();
        if (usuario == null) return "redirect:/login";

        Optional<Categoria> categoria = categoriaRepository.findById(id);
        if (categoria.isPresent() && categoria.get().getUsuario().getId().equals(usuario.getId())) {
            model.addAttribute("categoria", categoria.get());
            return "form_categoria";
        } else {
            return "redirect:/categorias";
        }
    }

    // ACTUALIZAR
    @PostMapping("/editar/{id}")
    public String actualizarCategoria(@PathVariable Long id, @ModelAttribute Categoria categoriaActualizada) {
        Usuario usuario = getUsuarioAutenticado();
        if (usuario == null) return "redirect:/login";

        Optional<Categoria> existente = categoriaRepository.findById(id);
        if (existente.isPresent() && existente.get().getUsuario().getId().equals(usuario.getId())) {
            categoriaActualizada.setId(id);
            categoriaActualizada.setUsuario(usuario);
            categoriaRepository.save(categoriaActualizada);
        }

        return "redirect:/categorias";
    }

    // ELIMINAR
    @GetMapping("/eliminar/{id}")
    public String eliminarCategoria(@PathVariable Long id) {
        Usuario usuario = getUsuarioAutenticado();
        if (usuario == null) return "redirect:/login";

        Optional<Categoria> categoria = categoriaRepository.findById(id);
        if (categoria.isPresent() && categoria.get().getUsuario().getId().equals(usuario.getId())) {
            categoriaRepository.deleteById(id);
        }

        return "redirect:/categorias";
    }
}
