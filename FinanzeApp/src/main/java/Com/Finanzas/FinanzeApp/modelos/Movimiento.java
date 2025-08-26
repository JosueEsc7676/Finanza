package Com.Finanzas.FinanzeApp.modelos;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Data;
@Data

@Entity
public class Movimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tipo; // "Ingreso" o "Egreso"

    private double monto;

    private LocalDate fecha;

    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @Transient
    private Long categoriaId; // Campo auxiliar para el formulario
    // 🔹 Relación con Meta
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meta_id")
    private Meta meta;



}
