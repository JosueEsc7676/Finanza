package Com.Finanzas.FinanzeApp.modelos;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "metas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Meta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String titulo;

    @Column(nullable = false)
    private double montoMeta;

    // 🔹 Este campo te faltaba
    @Column(nullable = false)
    private double montoActual = 0.0;  // acumulado con movimientos

    @Column(nullable = false)
    private LocalDate fechaLimite;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoMeta estado = EstadoMeta.PENDIENTE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "meta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Movimiento> movimientos = new ArrayList<>();

    // 🔹 Métodos utilitarios
    public void agregarMovimiento(Movimiento movimiento) {
        movimientos.add(movimiento);
        movimiento.setMeta(this);

        // actualizar el monto acumulado
        this.montoActual += movimiento.getMonto();

        // actualizar estado automáticamente
        if (montoActual >= montoMeta) {
            this.estado = EstadoMeta.COMPLETADA;
        } else {
            this.estado = EstadoMeta.EN_PROGRESO;
        }
    }

    // 🔹 Método extra para calcular progreso %
    public double getProgreso() {
        if (montoMeta <= 0) return 0;
        return (montoActual / montoMeta) * 100.0;
    }
    @OneToMany(mappedBy = "meta", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<Notificacion> notificaciones;
}

