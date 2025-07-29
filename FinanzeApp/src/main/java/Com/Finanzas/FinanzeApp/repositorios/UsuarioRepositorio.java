package Com.Finanzas.FinanzeApp.repositorios;

import Com.Finanzas.FinanzeApp.modelos.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {
    // Puedes agregar métodos personalizados como buscarPorCorreo si necesitas
}
