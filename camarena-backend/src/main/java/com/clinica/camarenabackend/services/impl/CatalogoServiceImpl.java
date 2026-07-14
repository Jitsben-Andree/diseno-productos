package com.clinica.camarenabackend.services.impl;

import com.clinica.camarenabackend.dtos.request.ExamenRequest;
import com.clinica.camarenabackend.dtos.request.ParametroRequest;
import com.clinica.camarenabackend.dtos.request.RangoRequest;
import com.clinica.camarenabackend.dtos.response.ExamenResponse;
import com.clinica.camarenabackend.dtos.response.ParametroResponse;
import com.clinica.camarenabackend.models.entities.CatalogoExamenes;
import com.clinica.camarenabackend.models.entities.ParametrosClinicos;
import com.clinica.camarenabackend.models.entities.RangosReferencia;
import com.clinica.camarenabackend.repositories.CatalogoExamenesRepository;
import com.clinica.camarenabackend.repositories.ParametrosClinicosRepository;
import com.clinica.camarenabackend.repositories.RangosReferenciaRepository;
import com.clinica.camarenabackend.services.interfaces.CatalogoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CatalogoServiceImpl implements CatalogoService {

    @Autowired
    private CatalogoExamenesRepository catalogoRepository;

    @Autowired
    private ParametrosClinicosRepository parametroRepository;

    @Autowired
    private RangosReferenciaRepository rangoRepository;

    @Override
    @Transactional
    public ExamenResponse crearExamen(ExamenRequest request) {
        if (catalogoRepository.existsByOcodigo(request.getCodigo())) {
            throw new RuntimeException("Error: El código de examen ya existe en el catálogo.");
        }

        CatalogoExamenes nuevoExamen = CatalogoExamenes.builder()
                .ocodigo(request.getCodigo())
                .odescripcion(request.getDescripcion())
                .otipoTuboDefecto(request.getTipoTuboDefecto())
                .oprecioBase(request.getPrecioBase())
                .build();

        CatalogoExamenes examenGuardado = catalogoRepository.save(nuevoExamen);
        return mapearAResponse(examenGuardado);
    }

    @Override
    public List<ExamenResponse> listarExamenes() {
        return catalogoRepository.findAll().stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void agregarParametroAExamen(Integer idExamen, ParametroRequest request) {
        CatalogoExamenes examen = catalogoRepository.findById(idExamen)
                .orElseThrow(() -> new RuntimeException("Error: Examen no encontrado."));

        // 1. Guardamos el parámetro (Nombre y Unidad)
        ParametrosClinicos parametro = ParametrosClinicos.builder()
                .examen(examen)
                .onombre(request.getNombre())
                .unidad(request.getUnidad())
                .build();

        ParametrosClinicos paramGuardado = parametroRepository.save(parametro);

        // 2. Guardamos INMEDIATAMENTE su Rango Normal con los valores que llegaron desde Angular
        RangosReferencia rango = RangosReferencia.builder()
                .parametro(paramGuardado)
                .osexoAplica(request.getSexoAplica() != null ? request.getSexoAplica() : "A")
                .oedadMinAnios(0)   // Edad por defecto (Desde bebés)
                .oedadMaxAnios(120) // Edad por defecto (Hasta ancianos)
                .ovalorMin(request.getValorMin())
                .ovalorMax(request.getValorMax())
                .build();

        rangoRepository.save(rango);
    }

    @Override
    @Transactional
    public void agregarRangoAParametro(Integer idParametro, RangoRequest request) {
        if (request.getEdadMinAnios() > request.getEdadMaxAnios()) {
            throw new RuntimeException("Error: La edad mínima no puede ser mayor a la máxima.");
        }
        if (request.getValorMin().compareTo(request.getValorMax()) > 0) {
            throw new RuntimeException("Error: El valor clínico mínimo no puede ser mayor al máximo.");
        }

        ParametrosClinicos parametro = parametroRepository.findById(idParametro)
                .orElseThrow(() -> new RuntimeException("Error: Parámetro no encontrado."));

        RangosReferencia rango = RangosReferencia.builder()
                .parametro(parametro)
                .osexoAplica(request.getSexoAplica().toUpperCase())
                .oedadMinAnios(request.getEdadMinAnios())
                .oedadMaxAnios(request.getEdadMaxAnios())
                .ovalorMin(request.getValorMin())
                .ovalorMax(request.getValorMax())
                .build();

        rangoRepository.save(rango);
    }


    // ====================================================================
    // LISTAR PARÁMETROS PARA EL MODAL DE ANGULAR
    // ====================================================================
    @Override
    public List<ParametroResponse> listarParametrosDeExamen(Integer idExamen) {
        List<ParametrosClinicos> parametros = parametroRepository.findByExamen_Oid_examen(idExamen);
        List<ParametroResponse> respuesta = new ArrayList<>();

        for (ParametrosClinicos param : parametros) {

            // Valores por defecto por si el parámetro no tiene rangos
            Double rMin = 0.0;
            Double rMax = 0.0;
            String sexo = "A";

            // Traemos el primer rango de referencia
            List<RangosReferencia> rangos = rangoRepository.findByParametro_Oid_parametro(param.getOid_parametro());
            if (!rangos.isEmpty()) {
                RangosReferencia rango = rangos.get(0);

                // OJO AQUÍ: Si te sigue marcando error en getOvalorMin(),
                // cámbialo por getValorMin() dependiendo de cómo esté en tu entidad.
                rMin = rango.getOvalorMin().doubleValue();
                rMax = rango.getOvalorMax().doubleValue();
                sexo = rango.getOsexoAplica();
            }

            // Construimos el Response usando Builder (más seguro y limpio)
            ParametroResponse response = ParametroResponse.builder()
                    .idParametro(param.getOid_parametro())
                    .nombre(param.getOnombre())
                    .unidad(param.getUnidad())
                    .rangoMin(rMin)
                    .rangoMax(rMax)
                    .sexoAplica(sexo)
                    .build();

            respuesta.add(response);
        }
        return respuesta;
    }

    @Override
    @Transactional
    public void actualizarParametro(Integer idParametro, ParametroRequest request) {
        ParametrosClinicos parametro = parametroRepository.findById(idParametro)
                .orElseThrow(() -> new RuntimeException("Error: Parámetro no encontrado."));

        parametro.setOnombre(request.getNombre());
        parametro.setUnidad(request.getUnidad());
        parametroRepository.save(parametro);

        List<RangosReferencia> rangos = rangoRepository.findByParametro_Oid_parametro(idParametro);
        if (!rangos.isEmpty()) {
            RangosReferencia rango = rangos.get(0);
            rango.setOvalorMin(request.getValorMin());
            rango.setOvalorMax(request.getValorMax());
            rango.setOsexoAplica(request.getSexoAplica() != null ? request.getSexoAplica() : "A");
            rangoRepository.save(rango);
        }
    }

    @Override
    @Transactional
    public void eliminarParametro(Integer idParametro) {
        ParametrosClinicos parametro = parametroRepository.findById(idParametro)
                .orElseThrow(() -> new RuntimeException("Error: Parámetro no encontrado."));

        try {
            List<RangosReferencia> rangos = rangoRepository.findByParametro_Oid_parametro(idParametro);
            rangoRepository.deleteAll(rangos);

            parametroRepository.delete(parametro);

            // Forzamos el commit en BD para atrapar el error de constraint de llave foránea si lo hay
            parametroRepository.flush();

        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("ACCIÓN DENEGADA: No se puede eliminar este parámetro porque ya cuenta con resultados registrados en el historial de pacientes.");
        }
    }

    // ====================================================================

    private ExamenResponse mapearAResponse(CatalogoExamenes examen) {
        return ExamenResponse.builder()
                .idExamen(examen.getOid_examen())
                .codigo(examen.getOcodigo())
                .descripcion(examen.getOdescripcion())
                .tipoTuboDefecto(examen.getOtipoTuboDefecto())
                .precioBase(examen.getOprecioBase())
                .build();
    }
}