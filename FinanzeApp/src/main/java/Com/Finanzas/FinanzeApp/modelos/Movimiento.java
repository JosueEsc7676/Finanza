package Com.Finanzas.FinanzeApp.modelos;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Movimiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tipo; // "Ingreso" o "Egreso"
    private double monto;
    private LocalDate fecha;
    private String descripcion;

    @ManyToOne
    private Usuario usuario;

    @ManyToOne
    private Categoria categoria;
}
