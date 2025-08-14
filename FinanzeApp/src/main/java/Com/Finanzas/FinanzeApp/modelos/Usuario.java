package Com.Finanzas.FinanzeApp.modelos;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String correo;
    private String contrasena;

    private String nombreCompleto;
    private String telefono;
    private String direccion;
    private String fechaNacimiento;
    private String ocupacion;
    private String genero;

    @Column(name = "token_recuperacion")
    private String tokenRecuperacion;

    @Column(name = "tema")
    private String tema; // valores posibles: "light", "dark"

    @Column(name = "modo_oscuro")
    private Boolean modoOscuro = false;

    @Lob
    @Column(name = "foto_perfil", columnDefinition = "LONGBLOB")
    private byte[] fotoPerfil; // Guardar imagen como BLOB

    @Column(name = "moneda", length = 10)
    private String moneda; // Ej: USD, EUR, MXN
}
