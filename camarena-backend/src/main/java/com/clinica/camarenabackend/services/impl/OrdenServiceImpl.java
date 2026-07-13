package com.clinica.camarenabackend.services.impl;

import com.clinica.camarenabackend.dtos.request.OrdenRequest;
import com.clinica.camarenabackend.dtos.request.PagoRequest;
import com.clinica.camarenabackend.dtos.response.OrdenResponse;
import com.clinica.camarenabackend.models.entities.*;
import com.clinica.camarenabackend.repositories.*;
import com.clinica.camarenabackend.services.interfaces.OrdenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrdenServiceImpl implements OrdenService {

    @Autowired
    private OrdenLaboratorioRepository ordenRepository;

    @Autowired
    private DetalleOrdenRepository detalleOrdenRepository;

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private CatalogoExamenesRepository catalogoExamenesRepository;

    @Override
    @Transactional // ¡CRÍTICO! Si falla un detalle, no se guarda la orden entera (ACID)
    public OrdenResponse crearOrden(OrdenRequest request) {

        // 1. Validar Paciente
        Paciente paciente = pacienteRepository.findByOdni(request.getDniPaciente())
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con DNI: " + request.getDniPaciente()));

        // 2. Crear la cabecera de la Orden
        String codigoTicket = "TKT-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        OrdenLaboratorio nuevaOrden = OrdenLaboratorio.builder()
                .paciente(paciente)
                .ocodigoTicket(codigoTicket)
                .ofechaEmision(LocalDateTime.now())
                .oestadoGeneral("PENDIENTE_PAGO")
                .build();

        OrdenLaboratorio ordenGuardada = ordenRepository.save(nuevaOrden);

        // 3. Procesar los Detalles (Exámenes) y calcular el total
        BigDecimal totalCalculado = BigDecimal.ZERO;

        for (Integer idExamen : request.getIdsExamenes()) {
            CatalogoExamenes examen = catalogoExamenesRepository.findById(idExamen)
                    .orElseThrow(() -> new RuntimeException("Examen no encontrado en catálogo con ID: " + idExamen));

            DetalleOrden detalle = DetalleOrden.builder()
                    .orden(ordenGuardada)
                    .examen(examen)
                    .oprecioCobrado(examen.getOprecioBase()) // REGLA CONTABLE: Congelamos el precio actual
                    .oestadoExamen("PENDIENTE_MUESTRA")
                    .build();

            detalleOrdenRepository.save(detalle);
            totalCalculado = totalCalculado.add(examen.getOprecioBase());
        }

        // 4. Retornar el Response para que el Recepcionista sepa cuánto cobrar
        return OrdenResponse.builder()
                .idOrden(ordenGuardada.getOid_orden())
                .nombrePaciente(paciente.getOnombres() + " " + paciente.getOapellidos())
                .codigoTicket(ordenGuardada.getOcodigoTicket())
                .fechaEmision(ordenGuardada.getOfechaEmision())
                .estadoGeneral(ordenGuardada.getOestadoGeneral())
                .montoTotalCalculado(totalCalculado)
                .build();
    }

    @Override
    @Transactional
    public void registrarPago(UUID idOrden, PagoRequest request) {
        // 1. Buscar la orden
        OrdenLaboratorio orden = ordenRepository.findById(idOrden)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

        if (!orden.getOestadoGeneral().equals("PENDIENTE_PAGO")) {
            throw new RuntimeException("Esta orden ya fue pagada o procesada.");
        }

        // 2. Crear el Pago
        Pago nuevoPago = Pago.builder()
                .orden(orden)
                .omontoTotal(request.getMontoTotal())
                .ometodoPago(request.getMetodoPago().toUpperCase())
                .nroComprobante(request.getNroComprobante())
                .fechaPago(LocalDateTime.now())
                .build();

        pagoRepository.save(nuevoPago);

        // 3. Actualizar el estado de la Orden a pagada para que pase a Tópico
        orden.setOestadoGeneral("EN_ESPERA_MUESTRA");
        ordenRepository.save(orden);
    }

    @Override
    public List<OrdenResponse> listarPendientesTopico() {
        // Buscamos todas las órdenes que el recepcionista ya cobró
        List<OrdenLaboratorio> pendientes = ordenRepository.findByOestadoGeneral("EN_ESPERA_MUESTRA");

        return pendientes.stream().map(orden -> OrdenResponse.builder()
                .idOrden(orden.getOid_orden())
                .nombrePaciente(orden.getPaciente().getOnombres() + " " + orden.getPaciente().getOapellidos())
                .codigoTicket(orden.getOcodigoTicket())
                .fechaEmision(orden.getOfechaEmision())
                .estadoGeneral(orden.getOestadoGeneral())
                .build()).collect(Collectors.toList());
    }

    // =================================================================================
    // NUEVOS MÉTODOS PARA EL HISTORIAL Y ANULACIONES
    // =================================================================================

    @Override
    public List<OrdenResponse> buscarHistorial(String filtro) {
        // Llama a la consulta omnicanal que creamos en el repositorio
        List<OrdenLaboratorio> ordenes = ordenRepository.buscarHistorial(filtro);

        return ordenes.stream().map(orden -> {
            // Calculamos el total de la orden sumando el precio cobrado de cada detalle
            BigDecimal total = detalleOrdenRepository.findByOrden_Oid_orden(orden.getOid_orden()).stream()
                    .map(DetalleOrden::getOprecioCobrado)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            return OrdenResponse.builder()
                    .idOrden(orden.getOid_orden())
                    .nombrePaciente(orden.getPaciente().getOnombres() + " " + orden.getPaciente().getOapellidos())
                    .codigoTicket(orden.getOcodigoTicket())
                    .fechaEmision(orden.getOfechaEmision())
                    .estadoGeneral(orden.getOestadoGeneral())
                    .montoTotalCalculado(total)
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional // Crítico porque modifica el estado en BD
    public void anularOrden(UUID idOrden, String motivo) {
        OrdenLaboratorio orden = ordenRepository.findById(idOrden)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

        // Regla de Seguridad Médica y Contable
        if (orden.getOestadoGeneral().equals("FINALIZADO") || orden.getOestadoGeneral().equals("ANULADO")) {
            throw new RuntimeException("No se puede anular esta orden en su estado actual.");
        }

        orden.setOestadoGeneral("ANULADO");
        ordenRepository.save(orden);

        // Opcional a futuro: Aquí podríamos guardar el motivo en una tabla de auditoría contable
    }
}