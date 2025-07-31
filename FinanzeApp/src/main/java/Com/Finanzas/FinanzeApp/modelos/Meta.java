package Com.Finanzas.FinanzeApp.modelos;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Meta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private double montoMeta;
    private LocalDate fechaLimite;

    @ManyToOne
    private Usuario usuario;
}
