package Com.Finanzas.FinanzeApp.modelos;

import java.math.BigDecimal;

public class PresupuestoEstadoDTO {
    private String categoria;
    private Double presupuesto;
    private Double gasto;
    private Double restante;
    private boolean excedido;
    private Double progreso; // 0-100

    public PresupuestoEstadoDTO(String categoria, Double presupuesto, Double gasto,
                                Double restante, boolean excedido, Double progreso) {
        this.categoria = categoria;
        this.presupuesto = presupuesto;
        this.gasto = gasto;
        this.restante = restante;
        this.excedido = excedido;
        this.progreso = progreso;
    }

    public String getCategoria() { return categoria; }
    public Double getPresupuesto() { return presupuesto; }
    public Double getGasto() { return gasto; }
    public Double getRestante() { return restante; }
    public boolean isExcedido() { return excedido; }
    public Double getProgreso() { return progreso; }
}
