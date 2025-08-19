package Com.Finanzas.FinanzeApp.modelos;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "presupuestos_categoria")
public class PresupuestoCategoria {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long usuarioId;
    private String categoria;

    @Column
    private Double presupuestoMensual;

    public PresupuestoCategoria() {}
    public PresupuestoCategoria(Long usuarioId, String categoria, Double presupuestoMensual) {
        this.usuarioId = usuarioId;
        this.categoria = categoria;
        this.presupuestoMensual = presupuestoMensual;
    }

    public Long getId() { return id; }
    public Long getUsuarioId() { return usuarioId; }
    public String getCategoria() { return categoria; }
    public Double getPresupuestoMensual() { return presupuestoMensual; }
}
