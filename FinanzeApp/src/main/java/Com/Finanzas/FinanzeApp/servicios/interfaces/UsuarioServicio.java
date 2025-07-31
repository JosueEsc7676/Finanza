package Com.Finanzas.FinanzeApp.servicios.interfaces;

import java.util.Optional;

import Com.Finanzas.FinanzeApp.modelos.Usuario;



public interface UsuarioServicio {
    Usuario registrar(Usuario usuario);
    Optional<Usuario> buscarPorCorreo(String correo);
    Optional<Usuario> buscarPorId(Long id);
    Usuario actualizar(Usuario usuario);
    boolean eliminarPorId(Long id);
    boolean verificarCredenciales(String correo, String contrasena);
}



