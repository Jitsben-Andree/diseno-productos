package com.clinica.camarenabackend.services.impl;

import com.clinica.camarenabackend.dtos.request.ExamenRequest;
import com.clinica.camarenabackend.dtos.request.ParametroRequest;
import com.clinica.camarenabackend.dtos.request.RangoRequest;
import com.clinica.camarenabackend.dtos.response.ExamenResponse;
import com.clinica.camarenabackend.models.entities.CatalogoExamenes;
import com.clinica.camarenabackend.models.entities.ParametrosClinicos;
import com.clinica.camarenabackend.models.entities.RangosReferencia;
import com.clinica.camarenabackend.repositories.CatalogoExamenesRepository;
import com.clinica.camarenabackend.repositories.ParametrosClinicosRepository;
import com.clinica.camarenabackend.repositories.RangosReferenciaRepository;
import com.clinica.camarenabackend.services.interfaces.CatalogoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        // Regla de Negocio: No permitir códigos duplicados
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

        ParametrosClinicos parametro = ParametrosClinicos.builder()
                .examen(examen)
                .onombre(request.getNombre())
                .unidad(request.getUnidad())
                .build();

        parametroRepository.save(parametro);
    }

    @Override
    @Transactional
    public void agregarRangoAParametro(Integer idParametro, RangoRequest request) {
        // Reglas de Negocio Lógicas
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

    // Método auxiliar (Mapper)
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