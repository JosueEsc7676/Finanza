package Com.Finanzas.FinanzeApp.repositorios;

import Com.Finanzas.FinanzeApp.modelos.Meta;
import Com.Finanzas.FinanzeApp.modelos.Notificacion;
import Com.Finanzas.FinanzeApp.modelos.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionMetaRepositorio extends JpaRepository<Notificacion, Long> {

    // 🔔 Todas las notificaciones de metas de un usuario
    List<Notificacion> findByMetaUsuarioOrderByFechaEnvioDesc(Usuario usuario);

    // 🔔 Todas las notificaciones asociadas a una meta específica
    List<Notificacion> findByMetaOrderByFechaEnvioDesc(Meta meta);
}
