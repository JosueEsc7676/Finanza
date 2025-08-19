package Com.Finanzas.FinanzeApp.repositorios;

import Com.Finanzas.FinanzeApp.modelos.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;


@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByCorreo(String correo);
 //buscar por token
    Optional<Usuario> findByTokenRecuperacion(String tokenRecuperacion);
}

