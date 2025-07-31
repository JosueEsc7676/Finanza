package Com.Finanzas.FinanzeApp.controladores;
import Com.Finanzas.FinanzeApp.repositorios.MovimientoRepository;

import Com.Finanzas.FinanzeApp.modelos.Movimiento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movimientos")
public class MovimientoController {

    @Autowired
    private MovimientoRepository repo;

    @PostMapping
    public Movimiento guardar(@RequestBody Movimiento mov) {
        return repo.save(mov);
    }

    @GetMapping("/usuario/{id}")
    public List<Movimiento> listarPorUsuario(@PathVariable Long id) {
        return repo.findByUsuarioId(id);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        repo.deleteById(id);
    }
}
