package com.clinica.camarenabackend.controllers;

import com.clinica.camarenabackend.dtos.response.DashboardStatsResponse;
import com.clinica.camarenabackend.models.entities.Pago;
import com.clinica.camarenabackend.repositories.MuestraClinicaRepository;
import com.clinica.camarenabackend.repositories.OrdenLaboratorioRepository;
import com.clinica.camarenabackend.repositories.PagoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DashboardController {

    @Autowired private OrdenLaboratorioRepository ordenRepo;
    @Autowired private PagoRepository pagoRepo;
    @Autowired private MuestraClinicaRepository muestraRepo;

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RECEPCION')")
    public ResponseEntity<DashboardStatsResponse> obtenerEstadisticas() {
        LocalDateTime inicioHoy = LocalDate.now().atStartOfDay();
        LocalDateTime finHoy = LocalDate.now().atTime(LocalTime.MAX);

        // 1. Cálculos de las Tarjetas Superiores
        List<Pago> pagosHoy = pagoRepo.findByFechaPagoBetween(inicioHoy, finHoy);
        BigDecimal totalIngresosHoy = pagosHoy.stream()
                .map(Pago::getOmontoTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int pendientesTopico = ordenRepo.findByOestadoGeneral("EN_ESPERA_MUESTRA").size();
        int resultadosListos = ordenRepo.findByOestadoGeneral("FINALIZADO").size();

        // 2. Cálculo del Gráfico (Últimos 7 días)
        LocalDateTime hace7Dias = LocalDate.now().minusDays(6).atStartOfDay();
        List<Object[]> ingresosAgrupados = pagoRepo.agruparIngresosPorDia(hace7Dias);

        List<String> etiquetas = new ArrayList<>();
        List<BigDecimal> valores = new ArrayList<>();

        DateTimeFormatter formatoDia = DateTimeFormatter.ofPattern("EEEE", new Locale("es", "ES"));

        // Llenamos el array garantizando el orden cronológico
        for (int i = 6; i >= 0; i--) {
            LocalDate fechaActual = LocalDate.now().minusDays(i);
            // Capitalizamos la primera letra (Ej: "Lunes" en vez de "lunes")
            String nombreDia = fechaActual.format(formatoDia);
            nombreDia = nombreDia.substring(0, 1).toUpperCase() + nombreDia.substring(1);
            etiquetas.add(nombreDia);

            // Buscamos si hubo ingresos en ese día
            BigDecimal montoDia = BigDecimal.ZERO;
            for (Object[] fila : ingresosAgrupados) {
                if (java.sql.Date.valueOf(fechaActual).toString().equals(fila[0].toString())) {
                    montoDia = (BigDecimal) fila[1];
                    break;
                }
            }
            valores.add(montoDia);
        }

        DashboardStatsResponse response = DashboardStatsResponse.builder()
                .ordenesHoy(pagosHoy.size()) // 1 pago = 1 orden facturada
                .ingresosHoy(totalIngresosHoy)
                .muestrasPendientes(pendientesTopico)
                .resultadosListos(resultadosListos)
                .etiquetasDias(etiquetas)
                .valoresIngresos(valores)
                .build();

        return ResponseEntity.ok(response);
    }
}
