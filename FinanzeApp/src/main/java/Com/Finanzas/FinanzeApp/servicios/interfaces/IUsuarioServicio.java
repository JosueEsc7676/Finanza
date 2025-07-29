package Com.Finanzas.FinanzeApp.servicios.interfaces;

import Com.Finanzas.FinanzeApp.modelos.Usuario;

import java.util.List;

public interface IUsuarioServicio {
    List<Usuario> obtenerTodos();
    Usuario guardar(Usuario usuario);
    Usuario buscarPorId(Long id);
    void eliminar(Long id);
}
