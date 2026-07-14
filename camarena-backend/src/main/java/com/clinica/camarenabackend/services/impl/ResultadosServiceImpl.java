package com.clinica.camarenabackend.services.impl;

import com.clinica.camarenabackend.dtos.request.IngresarResultadoRequest;
import com.clinica.camarenabackend.dtos.response.ParametroExamenResponse;
import com.clinica.camarenabackend.dtos.response.ResultadoResponse;
import com.clinica.camarenabackend.dtos.response.TuboPendienteResponse;
import com.clinica.camarenabackend.models.entities.*;
import com.clinica.camarenabackend.repositories.*;
import com.clinica.camarenabackend.services.interfaces.ResultadosService;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
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
                    .valorObtenido(null)
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
    public byte[] aprobarYGenerarPdf(UUID idOrden, String emailBiologo) {
        OrdenLaboratorio orden = ordenRepository.findById(idOrden)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

        Usuario usuarioBiologo = usuarioRepository.findByEmail(emailBiologo)
                .orElseThrow(() -> new RuntimeException("Usuario Biólogo no encontrado"));

        Empleado biologo = empleadoRepository.findByUsuario(usuarioBiologo)
                .orElseThrow(() -> new RuntimeException("El usuario '" + emailBiologo + "' no es un empleado."));

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

        // =================================================================================
        // GENERACIÓN DE PDF PROFESIONAL CON TODOS LOS DATOS
        // =================================================================================

        // Usamos el método que acabamos de crear en el repositorio
        List<ResultadosDatos> resultadosDeEstaOrden = resultadosRepository.buscarResultadosPorOrden(idOrden);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            // Fuentes
            Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font subtituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            Font redFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.RED);

            // Cabecera Institucional
            Paragraph titulo = new Paragraph("CLÍNICA CAMARENA", tituloFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);

            Paragraph subtitulo = new Paragraph("INFORME DE RESULTADOS DE LABORATORIO", subtituloFont);
            subtitulo.setAlignment(Element.ALIGN_CENTER);
            document.add(subtitulo);
            document.add(new Paragraph(" "));

            // ================= DATOS DEL PACIENTE Y ORDEN =================
            Paciente paciente = orden.getPaciente();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            // Cálculo de la edad
            int edad = Period.between(paciente.getOfechaNacimiento(), LocalDate.now()).getYears();
            String telefono = paciente.getTelefono() != null ? paciente.getTelefono() : "No registrado";
            String cmpBiologo = biologo.getCmpColegiatura() != null ? biologo.getCmpColegiatura() : "Pendiente";

            PdfPTable datosGenerales = new PdfPTable(2);
            datosGenerales.setWidthPercentage(100);
            datosGenerales.setWidths(new float[]{1f, 1f});

            // Columna Izquierda: Paciente
            PdfPCell celdaIzq = new PdfPCell();
            celdaIzq.setBorder(PdfPCell.NO_BORDER);
            celdaIzq.addElement(new Paragraph("DATOS DEL PACIENTE", boldFont));
            celdaIzq.addElement(new Paragraph("Nombre: " + paciente.getOnombres() + " " + paciente.getOapellidos(), normalFont));
            celdaIzq.addElement(new Paragraph("DNI: " + paciente.getOdni(), normalFont));
            celdaIzq.addElement(new Paragraph("Edad: " + edad + " años | Sexo: " + paciente.getOsexo(), normalFont));
            celdaIzq.addElement(new Paragraph("Teléfono: " + telefono, normalFont));
            datosGenerales.addCell(celdaIzq);

            // Columna Derecha: Orden y Biólogo
            PdfPCell celdaDer = new PdfPCell();
            celdaDer.setBorder(PdfPCell.NO_BORDER);
            celdaDer.addElement(new Paragraph("DATOS DE LA ORDEN", boldFont));
            celdaDer.addElement(new Paragraph("Ticket Nº: " + orden.getOcodigoTicket(), normalFont));
            celdaDer.addElement(new Paragraph("Fecha de Toma: " + orden.getOfechaEmision().format(formatter), normalFont));
            celdaDer.addElement(new Paragraph("Fecha de Informe: " + LocalDateTime.now().format(formatter), normalFont));
            celdaDer.addElement(new Paragraph("Biólogo: " + biologo.getOnombres() + " " + biologo.getOapellidos() + " (CMP: " + cmpBiologo + ")", normalFont));
            datosGenerales.addCell(celdaDer);

            document.add(datosGenerales);
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            // ================= TABLA DE RESULTADOS =================
            // Ahora tiene 6 columnas para incluir un indicador visual extra de anomalías
            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{2.5f, 2.5f, 1.5f, 1f, 2f, 1.5f}); // Anchos ajustados

            String[] cabeceras = {"Examen", "Parámetro", "Resultado", "Unidad", "Valores de Referencia", "Observación"};
            for (String cabecera : cabeceras) {
                PdfPCell cell = new PdfPCell(new Phrase(cabecera, boldFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(new Color(230, 230, 230)); // Gris claro
                cell.setPadding(6);
                table.addCell(cell);
            }

            // Llenado dinámico de datos
            for (ResultadosDatos res : resultadosDeEstaOrden) {
                boolean anormal = res.getOesAnormal() != null && res.getOesAnormal();
                Font fuenteResultado = anormal ? redFont : normalFont;

                // 1. Examen
                table.addCell(new Phrase(res.getDetalleOrden().getExamen().getOdescripcion(), normalFont));
                // 2. Parámetro
                table.addCell(new Phrase(res.getParametro().getOnombre(), normalFont));

                // 3. Resultado
                PdfPCell valorCell = new PdfPCell(new Phrase(String.valueOf(res.getOvalorObtenido()), fuenteResultado));
                valorCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(valorCell);

                // 4. Unidad
                PdfPCell unidadCell = new PdfPCell(new Phrase(res.getParametro().getUnidad(), normalFont));
                unidadCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(unidadCell);

                // 5. Rango de Referencia
                List<RangosReferencia> rangos = rangosRepository.findByParametro_Oid_parametro(res.getParametro().getOid_parametro());
                RangosReferencia rangoAplica = rangos.stream()
                        .filter(r -> r.getOsexoAplica().equals("A") || r.getOsexoAplica().equals(paciente.getOsexo()))
                        .findFirst().orElse(null);

                String rangoTexto = (rangoAplica != null) ? rangoAplica.getOvalorMin() + " - " + rangoAplica.getOvalorMax() : "N/D";
                PdfPCell rangoCell = new PdfPCell(new Phrase(rangoTexto, normalFont));
                rangoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(rangoCell);

                // 6. Observación (Normal/Fuera de Rango)
                String obsTexto = anormal ? "Fuera de Rango" : "Normal";
                PdfPCell obsCell = new PdfPCell(new Phrase(obsTexto, fuenteResultado));
                obsCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(obsCell);
            }

            document.add(table);

            // ================= PIE DE PÁGINA =================
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            Paragraph validacion = new Paragraph("Documento firmado digitalmente por el sistema del Laboratorio Clínico Camarena.", normalFont);
            validacion.setAlignment(Element.ALIGN_CENTER);
            document.add(validacion);

            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar el archivo PDF: " + e.getMessage());
        }
    }
}