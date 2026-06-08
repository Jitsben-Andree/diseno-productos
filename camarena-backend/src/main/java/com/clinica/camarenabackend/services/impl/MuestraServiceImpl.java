package com.clinica.camarenabackend.services.impl;

import com.clinica.camarenabackend.dtos.response.MuestraResponse;
import com.clinica.camarenabackend.services.interfaces.MuestraService;

import com.clinica.camarenabackend.models.entities.OrdenLaboratorio;
import com.clinica.camarenabackend.models.entities.DetalleOrden;
import com.clinica.camarenabackend.models.entities.MuestraClinica;
import com.clinica.camarenabackend.models.entities.ExamenInsumo;
import com.clinica.camarenabackend.models.entities.InventarioInsumos;
import com.clinica.camarenabackend.models.entities.CatalogoExamenes;
import com.clinica.camarenabackend.models.entities.Paciente;

import com.clinica.camarenabackend.repositories.OrdenLaboratorioRepository;
import com.clinica.camarenabackend.repositories.DetalleOrdenRepository;
import com.clinica.camarenabackend.repositories.MuestraClinicaRepository;
import com.clinica.camarenabackend.repositories.ExamenInsumoRepository;
import com.clinica.camarenabackend.repositories.InventarioInsumosRepository;

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

        // 1. SOLUCIÓN AL ERROR 403 (Duplicados):
        // Verificamos si esta orden ya tiene muestras generadas previamente
        List<MuestraClinica> muestrasExistentes = muestraRepository.findByDetalleOrden_Orden_Oid_orden(idOrden);

        if (!muestrasExistentes.isEmpty()) {
            // Si ya existen, simplemente las devolvemos para que el técnico continúe su trabajo
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

        // Marcar como tomada
        muestra.setOestadoMuestra("TOMADA");

        // Descontar Inventario Automáticamente
        CatalogoExamenes examen = muestra.getDetalleOrden().getExamen();
        List<ExamenInsumo> recetaInsumos = examenInsumoRepository.findByExamen_Oid_examen(examen.getOid_examen());

        for (ExamenInsumo receta : recetaInsumos) {
            InventarioInsumos insumo = receta.getInsumo();
            int nuevoStock = insumo.getOstockActual() - receta.getOcantidadRequerida().intValue();

            if (nuevoStock < 0) {
                throw new RuntimeException("Error: Stock insuficiente para el insumo: " + insumo.getOnombreInsumo());
            }

            insumo.setOstockActual(nuevoStock);
            inventarioRepository.save(insumo);
        }

        // Cambiamos el estado del detalle de la orden a EN_PROCESO
        DetalleOrden detalle = muestra.getDetalleOrden();
        detalle.setOestadoExamen("EN_PROCESO");
        detalleOrdenRepository.save(detalle);

        MuestraClinica muestraActualizada = muestraRepository.save(muestra);

        // 2. SOLUCIÓN AL BUCLE DE ESPERA:
        // Verificamos si, con esta muestra que acabamos de tomar, el paciente ya completó TODOS sus tubos
        UUID idOrden = detalle.getOrden().getOid_orden();
        List<MuestraClinica> todasLasMuestras = muestraRepository.findByDetalleOrden_Orden_Oid_orden(idOrden);

        boolean todasTomadas = todasLasMuestras.stream()
                .allMatch(m -> m.getOestadoMuestra().equals("TOMADA"));

        if (todasTomadas) {
            // Si ya se sacaron todos los tubos, cambiamos la Orden General de estado
            OrdenLaboratorio orden = detalle.getOrden();
            orden.setOestadoGeneral("MUESTRAS_TOMADAS"); // Esto hace que ya no aparezca en "En Espera"
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