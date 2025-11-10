package Com.Finanzas.FinanzeApp.modelos;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TopGastoDTO {
    private String descripcion;
    private String categoria;
    private LocalDate fecha;
    private Double monto;

    public TopGastoDTO(String descripcion, String categoria, LocalDate fecha, Double monto) {
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.fecha = fecha;
        this.monto = monto;
    }

    public String getDescripcion() { return descripcion; }
    public String getCategoria() { return categoria; }
    public LocalDate getFecha() { return fecha; }
    public Double getMonto() { return monto; }
}
