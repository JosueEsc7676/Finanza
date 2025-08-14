package Com.Finanzas.FinanzeApp.Config;

import Com.Finanzas.FinanzeApp.modelos.Usuario;
import Com.Finanzas.FinanzeApp.repositorios.UsuarioRepositorio;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class InicializadorUsuario {

    @Bean
    public CommandLineRunner crearUsuarioInicial(UsuarioRepositorio repo) {
        return args -> {
            if (repo.findByCorreo("admin@finanzas.com").isEmpty()) {
                Usuario usuario = new Usuario();
                usuario.setNombreCompleto("Administrador del Sistema");
                usuario.setCorreo("admin@finanzas.com");
                usuario.setContrasena(new BCryptPasswordEncoder().encode("admin123"));

                // Datos adicionales
                usuario.setTelefono("0000-0000");
                usuario.setDireccion("Ciudad Arce, La Libertad");
                usuario.setFechaNacimiento("1980-01-01");
                usuario.setOcupacion("Administrador");
                usuario.setGenero("Masculino");

                repo.save(usuario);
                System.out.println("Usuario inicial creado correctamente.");
            }
        };
    }
}
