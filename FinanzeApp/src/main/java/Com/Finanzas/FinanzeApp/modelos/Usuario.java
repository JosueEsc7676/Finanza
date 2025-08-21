package Com.Finanzas.FinanzeApp.modelos;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

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
    private boolean datosCompletos = false; // 🔑 Nuevo campo

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

    // 🔹 Nuevo: identificar proveedor OAuth2 (ej: GOOGLE, FACEBOOK, etc.)
    @Column(name = "proveedor")
    private String proveedor;
    @Column(name = "foto_url")
    private String fotoUrl; // si viene de Google, guardamos aquí la URL

    // 🔹 Nuevo: id del proveedor (sub de Google u otro)
    @Column(name = "proveedor_id")
    private String proveedorId;
    // Aquí pones la relación con Movimiento:
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Movimiento> movimientos;
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Categoria> categorias;
    @Column(name = "notificaciones_activas")
    private Boolean notificacionesActivas=true;
    @Column(name = "ultimo_recordatorio")
    private java.time.LocalDateTime ultimoRecordatorio;
}
