package Com.Finanzas.FinanzeApp.repositorios;

import Com.Finanzas.FinanzeApp.modelos.Notificacion;
import Com.Finanzas.FinanzeApp.modelos.Usuario;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NotificacionRepositorio extends JpaRepository<Notificacion, Long> {

    // Listar todas las notificaciones de un usuario con orden dinámico
    List<Notificacion> findAllByUsuario(Usuario usuario, Sort sort);
    long countByUsuario(Usuario usuario);
    // Listar todas las notificaciones sin orden explícito (usa el default de JPA)
    List<Notificacion> findByUsuario(Usuario usuario);

    // 🔔 Contar solo las NO leídas
    long countByUsuarioAndLeidaFalse(Usuario usuario);

    // 🔔 Traer solo las NO leídas
    List<Notificacion> findByUsuarioAndLeidaFalse(Usuario usuario);

    // Borrar todas las notificaciones de un usuario
    @Transactional
    void deleteAllByUsuario(Usuario usuario);
}
