package com.clinica.camarenabackend.services.impl;

import com.clinica.camarenabackend.dtos.request.IngresarResultadoRequest;
import com.clinica.camarenabackend.dtos.response.ParametroExamenResponse;
import com.clinica.camarenabackend.dtos.response.ResultadoResponse;
import com.clinica.camarenabackend.dtos.response.TuboPendienteResponse;
import com.clinica.camarenabackend.models.entities.*;
import com.clinica.camarenabackend.repositories.*;
import com.clinica.camarenabackend.services.interfaces.NotificacionService;
import com.clinica.camarenabackend.services.interfaces.ResultadosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ResultadosServiceImpl implements ResultadosService {

    @Autowired private ResultadosDatosRepository resultadosRepository;
    @Autowired private DetalleOrdenRepository detalleRepository;
    @Autowired private ParametrosClinicosRepository parametroRepository;
    @Autowired private RangosReferenciaRepository rangosRepository;
    @Autowired private OrdenLaboratorioRepository ordenRepository;
    @Autowired private ResultadosPdfRepository pdfRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private EmpleadoRepository empleadoRepository;
    @Autowired private MuestraClinicaRepository muestraRepository;
    @Autowired private NotificacionService notificacionService;

    @Override
    public List<TuboPendienteResponse> listarTubosPendientes() {
        List<MuestraClinica> muestras = muestraRepository.findMuestrasParaBiologo();

        return muestras.stream().map(m -> {
            Paciente p = m.getDetalleOrden().getOrden().getPaciente();
            String infoPaciente = p.getOnombres() + " " + p.getOapellidos() + " (Sexo: " + p.getOsexo() + ")";

            return TuboPendienteResponse.builder()
                    .idMuestra(m.getOid_muestra())
                    .idOrdenCorto(m.getDetalleOrden().getOrden().getOcodigoTicket())
                    .idOrdenReal(m.getDetalleOrden().getOrden().getOid_orden())
                    .codigoBarras(m.getOcodigoBarras())
                    .nombreExamen(m.getDetalleOrden().getExamen().getOdescripcion())
                    .paciente(infoPaciente)
                    .prioridad(m.getDetalleOrden().getOrden().getOestadoGeneral().contains("URGENTE") ? "URGENTE" : "NORMAL")
                    .idDetalleOrden(m.getDetalleOrden().getOid_detalle())
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public List<ParametroExamenResponse> obtenerParametrosDeExamen(UUID idDetalleOrden) {
        DetalleOrden detalle = detalleRepository.findById(idDetalleOrden)
                .orElseThrow(() -> new RuntimeException("Detalle de orden no encontrado"));

        List<ParametrosClinicos> parametros = parametroRepository.findByExamen_Oid_examen(detalle.getExamen().getOid_examen());
        String sexoPaciente = detalle.getOrden().getPaciente().getOsexo();

        return parametros.stream().map(p -> {
            // Buscamos los rangos normales para el sexo del paciente
            List<RangosReferencia> rangos = rangosRepository.findByParametro_Oid_parametro(p.getOid_parametro());
            RangosReferencia rangoAplica = rangos.stream()
                    .filter(r -> r.getOsexoAplica().equals("A") || r.getOsexoAplica().equals(sexoPaciente))
                    .findFirst().orElse(null);

            BigDecimal min = rangoAplica != null ? rangoAplica.getOvalorMin() : BigDecimal.ZERO;
            BigDecimal max = rangoAplica != null ? rangoAplica.getOvalorMax() : BigDecimal.ZERO;

            return ParametroExamenResponse.builder()
                    .idParametro(p.getOid_parametro())
                    .nombre(p.getOnombre())
                    .unidad(p.getUnidad())
                    .rangoMin(min)
                    .rangoMax(max)
                    .valorObtenido(null) // Para que Angular muestre el input vacío
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ResultadoResponse ingresarValorAnalitico(IngresarResultadoRequest request) {
        DetalleOrden detalle = detalleRepository.findById(request.getIdDetalleOrden())
                .orElseThrow(() -> new RuntimeException("Detalle no encontrado"));
        ParametrosClinicos parametro = parametroRepository.findById(request.getIdParametro())
                .orElseThrow(() -> new RuntimeException("Parámetro no encontrado"));

        // Calculamos si está fuera de rango
        String sexo = detalle.getOrden().getPaciente().getOsexo();
        List<RangosReferencia> rangos = rangosRepository.findByParametro_Oid_parametro(parametro.getOid_parametro());
        RangosReferencia rangoAplica = rangos.stream()
                .filter(r -> r.getOsexoAplica().equals("A") || r.getOsexoAplica().equals(sexo))
                .findFirst().orElse(null);

        boolean esAnormal = false;
        String rangoRefStr = "No definido";

        if (rangoAplica != null) {
            rangoRefStr = rangoAplica.getOvalorMin() + " - " + rangoAplica.getOvalorMax();
            BigDecimal valorMin = rangoAplica.getOvalorMin();
            BigDecimal valorMax = rangoAplica.getOvalorMax();
            BigDecimal valorActual = request.getValorObtenido();

            if (valorActual.compareTo(valorMin) < 0 || valorActual.compareTo(valorMax) > 0) {
                esAnormal = true;
            }
        }

        ResultadosDatos resultado = ResultadosDatos.builder()
                .detalleOrden(detalle)
                .parametro(parametro)
                .ovalorObtenido(request.getValorObtenido())
                .oesAnormal(esAnormal)
                .build();

        resultadosRepository.save(resultado);

        return ResultadoResponse.builder()
                .idResultado(resultado.getOid_resultado_dato())
                .nombreParametro(parametro.getOnombre())
                .valorObtenido(resultado.getOvalorObtenido())
                .unidadMedida(parametro.getUnidad())
                .esAnormal(esAnormal)
                .rangoNormalReferencia(rangoRefStr)
                .build();
    }

    @Override
    @Transactional
    public void aprobarYGenerarPdf(UUID idOrden, String emailBiologo) {
        OrdenLaboratorio orden = ordenRepository.findById(idOrden)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));
        Usuario usuarioBiologo = usuarioRepository.findByEmail(emailBiologo)
                .orElseThrow(() -> new RuntimeException("Usuario Biólogo no encontrado"));
        Empleado biologo = empleadoRepository.findByUsuario(usuarioBiologo)
                .orElseThrow(() -> new RuntimeException("Perfil de Empleado no encontrado"));

        orden.setOestadoGeneral("FINALIZADO");
        ordenRepository.save(orden);

        ResultadosPdf pdf = ResultadosPdf.builder()
                .orden(orden)
                .biologo(biologo)
                .opdfUrl("https://storage.camarena.com/resultados/" + orden.getOcodigoTicket() + ".pdf")
                .estado("GENERADO")
                .fechaAprobacion(LocalDateTime.now())
                .build();
        pdfRepository.save(pdf);

        notificacionService.enviarNotificacionResultadosListos(orden);
    }
}