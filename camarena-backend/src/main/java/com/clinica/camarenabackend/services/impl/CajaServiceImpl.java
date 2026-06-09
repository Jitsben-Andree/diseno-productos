package com.clinica.camarenabackend.services.impl;

import com.clinica.camarenabackend.dtos.response.CierreCajaResponse;
import com.clinica.camarenabackend.models.entities.Pago;
import com.clinica.camarenabackend.repositories.PagoRepository;
import com.clinica.camarenabackend.services.interfaces.CajaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CajaServiceImpl implements CajaService {

    @Autowired
    private PagoRepository pagoRepository;

    @Override
    public List<CierreCajaResponse> obtenerCierreDiario(String fechaStr) {
        // 1. Convertimos la fecha de texto (Ej. "2026-06-09") a LocalDateTime
        LocalDate fecha = LocalDate.parse(fechaStr);
        LocalDateTime inicioDelDia = fecha.atStartOfDay(); // 00:00:00
        LocalDateTime finDelDia = fecha.atTime(LocalTime.MAX); // 23:59:59.999

        // 2. Buscamos en PostgreSQL todos los pagos de ese día
        List<Pago> pagosDelDia = pagoRepository.findByFechaPagoBetween(inicioDelDia, finDelDia);

        // 3. Mapeamos la data pura al formato que requiere Angular para el Excel
        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");

        return pagosDelDia.stream().map(pago -> CierreCajaResponse.builder()
                .ticket(pago.getOrden().getOcodigoTicket())
                .paciente(pago.getOrden().getPaciente().getOnombres() + " " + pago.getOrden().getPaciente().getOapellidos())
                .dni(pago.getOrden().getPaciente().getOdni())
                .fecha(pago.getFechaPago().format(formatoHora))
                .metodoPago(pago.getOmetodoPago())
                .monto(pago.getOmontoTotal())
                .cajero("Recepción Central") // Fijo por ahora, escalable a futuro
                .build()
        ).collect(Collectors.toList());
    }
}