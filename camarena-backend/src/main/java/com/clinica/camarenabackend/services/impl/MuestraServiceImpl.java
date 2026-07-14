package com.clinica.camarenabackend.services.impl;

import com.clinica.camarenabackend.dtos.response.MuestraResponse;
import com.clinica.camarenabackend.services.interfaces.MuestraService;
import com.clinica.camarenabackend.models.entities.*;
import com.clinica.camarenabackend.repositories.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MuestraServiceImpl implements MuestraService {

    @Autowired
    private OrdenLaboratorioRepository ordenRepository;
    @Autowired
    private DetalleOrdenRepository detalleOrdenRepository;
    @Autowired
    private MuestraClinicaRepository muestraRepository;
    @Autowired
    private ExamenInsumoRepository examenInsumoRepository;
    @Autowired
    private InventarioInsumosRepository inventarioRepository;

    @Override
    @Transactional
    public List<MuestraResponse> generarMuestrasParaOrden(UUID idOrden) {
        OrdenLaboratorio orden = ordenRepository.findById(idOrden)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

        // Verificamos si esta orden ya tiene muestras generadas (Evitar duplicados)
        List<MuestraClinica> muestrasExistentes = muestraRepository.findByDetalleOrden_Orden_Oid_orden(idOrden);
        if (!muestrasExistentes.isEmpty()) {
            return muestrasExistentes.stream().map(this::mapearAResponse).collect(Collectors.toList());
        }

        if (!orden.getOestadoGeneral().equals("EN_ESPERA_MUESTRA")) {
            throw new RuntimeException("La orden debe estar pagada y en espera de muestra para generar códigos.");
        }

        List<DetalleOrden> detalles = detalleOrdenRepository.findByOrden_Oid_orden(idOrden);
        List<MuestraResponse> respuestas = new ArrayList<>();

        for (DetalleOrden detalle : detalles) {
            String codigoBarras = "BAR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            MuestraClinica nuevaMuestra = MuestraClinica.builder()
                    .detalleOrden(detalle)
                    .ocodigoBarras(codigoBarras)
                    .oestadoMuestra("PENDIENTE")
                    .build();

            MuestraClinica muestraGuardada = muestraRepository.save(nuevaMuestra);
            respuestas.add(mapearAResponse(muestraGuardada));
        }
        return respuestas;
    }

    @Override
    @Transactional
    public MuestraResponse marcarComoTomada(UUID idMuestra) {
        MuestraClinica muestra = muestraRepository.findById(idMuestra)
                .orElseThrow(() -> new RuntimeException("Muestra no encontrada"));

        if (!muestra.getOestadoMuestra().equals("PENDIENTE")) {
            throw new RuntimeException("Esta muestra ya fue procesada o tomada.");
        }

        // 1. DESCUENTO AUTOMÁTICO DE INVENTARIO
        CatalogoExamenes examen = muestra.getDetalleOrden().getExamen();
        List<ExamenInsumo> recetaInsumos = examenInsumoRepository.findByExamen_Oid_examen(examen.getOid_examen());

        for (ExamenInsumo receta : recetaInsumos) {
            InventarioInsumos insumo = receta.getInsumo();
            int nuevoStock = insumo.getOstockActual() - receta.getOcantidadRequerida().intValue();

            // Si no hay stock, se aborta toda la transacción y el frontend muestra el error rojo
            if (nuevoStock < 0) {
                throw new RuntimeException("Error: Stock insuficiente para el insumo: " + insumo.getOnombreInsumo());
            }

            insumo.setOstockActual(nuevoStock);
            inventarioRepository.save(insumo);
        }

        // 2. ACTUALIZACIÓN DE ESTADOS
        muestra.setOestadoMuestra("TOMADA");

        DetalleOrden detalle = muestra.getDetalleOrden();
        detalle.setOestadoExamen("EN_PROCESO");
        detalleOrdenRepository.save(detalle);

        MuestraClinica muestraActualizada = muestraRepository.save(muestra);

        // 3. VERIFICAR SI EL PACIENTE COMPLETÓ TODA LA EXTRACCIÓN
        UUID idOrden = detalle.getOrden().getOid_orden();
        List<MuestraClinica> todasLasMuestras = muestraRepository.findByDetalleOrden_Orden_Oid_orden(idOrden);

        boolean todasTomadas = todasLasMuestras.stream()
                .allMatch(m -> m.getOestadoMuestra().equals("TOMADA"));

        if (todasTomadas) {
            OrdenLaboratorio orden = detalle.getOrden();
            orden.setOestadoGeneral("MUESTRAS_TOMADAS"); // Sale del tablero de espera
            ordenRepository.save(orden);
        }

        return mapearAResponse(muestraActualizada);
    }

    private MuestraResponse mapearAResponse(MuestraClinica muestra) {
        Paciente paciente = muestra.getDetalleOrden().getOrden().getPaciente();
        CatalogoExamenes examen = muestra.getDetalleOrden().getExamen();

        return MuestraResponse.builder()
                .idMuestra(muestra.getOid_muestra())
                .codigoBarras(muestra.getOcodigoBarras())
                .estadoMuestra(muestra.getOestadoMuestra())
                .nombreExamen(examen.getOdescripcion())
                .tipoTuboRequerido(examen.getOtipoTuboDefecto())
                .nombrePaciente(paciente.getOnombres() + " " + paciente.getOapellidos())
                .build();
    }
}