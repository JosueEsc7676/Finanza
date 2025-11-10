package Com.Finanzas.FinanzeApp.modelos;

import java.math.BigDecimal;

public class KPIAnalisisDTO {
    private Double totalGastos;
    private String categoriaMasCostosa;
    private Double variacionMesAnterior; // % (+/-)
    private Double promedioDiario;
    private Double porcentajeAhorro; // opcional si manejas ingresos

    public KPIAnalisisDTO(Double totalGastos, String categoriaMasCostosa, Double variacionMesAnterior,
                          Double promedioDiario, Double porcentajeAhorro) {
        this.totalGastos = totalGastos;
        this.categoriaMasCostosa = categoriaMasCostosa;
        this.variacionMesAnterior = variacionMesAnterior;
        this.promedioDiario = promedioDiario;
        this.porcentajeAhorro = porcentajeAhorro;
    }

    public Double getTotalGastos() { return totalGastos; }
    public String getCategoriaMasCostosa() { return categoriaMasCostosa; }
    public Double getVariacionMesAnterior() { return variacionMesAnterior; }
    public Double getPromedioDiario() { return promedioDiario; }
    public Double getPorcentajeAhorro() { return porcentajeAhorro; }
}
