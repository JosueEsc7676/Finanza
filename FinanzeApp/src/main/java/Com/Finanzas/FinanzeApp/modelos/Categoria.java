package Com.Finanzas.FinanzeApp.modelos;
import java.util.List;

import jakarta.persistence.*;
@Entity
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String tipo; // "Ingreso" o "Egreso"

    @OneToMany(mappedBy = "categoria")
    private List<Movimiento> movimientos;
}
