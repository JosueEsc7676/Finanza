package Com.Finanzas.FinanzeApp.repositorios;

import Com.Finanzas.FinanzeApp.modelos.PresupuestoCategoria;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface PresupuestoCategoriaRepository extends CrudRepository<PresupuestoCategoria, Long> {
    List<PresupuestoCategoria> findByUsuarioId(Long usuarioId);
    PresupuestoCategoria findByUsuarioIdAndCategoria(Long usuarioId, String categoria);
}
