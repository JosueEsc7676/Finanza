package Com.Finanzas.FinanzeApp.modelos;
import jakarta.persistence.*;

@Entity
public class Consejo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String contenido;
    private String categoria;
}
