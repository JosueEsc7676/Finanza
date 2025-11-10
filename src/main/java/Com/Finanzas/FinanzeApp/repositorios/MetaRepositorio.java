package Com.Finanzas.FinanzeApp.repositorios;

import Com.Finanzas.FinanzeApp.modelos.Meta;
import Com.Finanzas.FinanzeApp.modelos.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MetaRepositorio extends JpaRepository<Meta, Long> {
    // Buscar todas las metas de un usuario
    List<Meta> findByUsuario(Usuario usuario);
    @Query("SELECT m FROM Meta m JOIN FETCH m.usuario")
    List<Meta> findAllWithUsuario();

    // Buscar metas por estado
    List<Meta> findByEstado(String estado);
}
