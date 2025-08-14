package Com.Finanzas.FinanzeApp.modelos;

import java.math.BigDecimal;

public class GastoCategoriaDTO {
    private String categoria;
    private Double monto;
    private Double porcentaje;

    public GastoCategoriaDTO(String categoria, Double monto, Double porcentaje) {
        this.categoria = categoria;
        this.monto = monto;
        this.porcentaje = porcentaje;
    }

    public String getCategoria() { return categoria; }
    public Double getMonto() { return monto; }
    public Double getPorcentaje() { return porcentaje; }
}


