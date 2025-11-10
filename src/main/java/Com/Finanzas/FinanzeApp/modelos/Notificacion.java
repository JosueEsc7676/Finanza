package Com.Finanzas.FinanzeApp.modelos;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "notificaciones")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String correo;
    private String asunto;

    @Column(columnDefinition = "TEXT")
    private String mensaje;

    private LocalDateTime fechaEnvio;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "meta_id", nullable = true) // Puede ser null si no está asociada a una meta
    private Meta meta;

    public Notificacion() {}
    private Boolean leida = false;
    public Notificacion(String correo, String asunto, String mensaje, LocalDateTime fechaEnvio) {
        this.correo = correo;
        this.asunto = asunto;
        this.mensaje = mensaje;
        this.fechaEnvio = fechaEnvio;
    }
}
