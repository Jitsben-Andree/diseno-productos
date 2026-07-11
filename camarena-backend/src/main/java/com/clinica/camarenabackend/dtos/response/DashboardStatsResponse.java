package com.clinica.camarenabackend.dtos.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class DashboardStatsResponse {
    // Tarjetas Superiores
    private Integer ordenesHoy;
    private BigDecimal ingresosHoy;
    private Integer muestrasPendientes;
    private Integer resultadosListos;

    // Datos dinámicos para el gráfico de barras (Últimos 7 días)
    private List<String> etiquetasDias; // Ej: ["Lunes", "Martes", "Miércoles"...]
    private List<BigDecimal> valoresIngresos; // Ej: [850.50, 1200.00, 950.00...]
}