package Com.Finanzas.FinanzeApp.modelos;

import jakarta.persistence.*;
@Entity
public class Presupuesto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double limiteMensual;
    private String mes;

    @ManyToOne
    private Usuario usuario;

    @ManyToOne
    private Categoria categoria;
}
