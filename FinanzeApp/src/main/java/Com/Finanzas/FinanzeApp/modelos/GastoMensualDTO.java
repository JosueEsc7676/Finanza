package Com.Finanzas.FinanzeApp.modelos;

import java.math.BigDecimal;

public class GastoMensualDTO {
    private String periodo; // ej: "2025-08"
    private Double total;

    public GastoMensualDTO(String periodo, Double total) {
        this.periodo = periodo;
        this.total = total;
    }

    public String getPeriodo() { return periodo; }
    public Double getTotal() { return total; }
}
