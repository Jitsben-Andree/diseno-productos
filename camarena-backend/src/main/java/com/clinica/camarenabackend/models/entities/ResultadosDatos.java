package com.clinica.camarenabackend.models.entities;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "resultados_datos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResultadosDatos {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID oid_resultado_dato;

    // Relación: Estos valores pertenecen a un detalle (examen) de una orden
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oid_detalle", nullable = false)
    private DetalleOrden detalleOrden;

    // Relación: Identifica qué parámetro exacto se midió (Ej. Glucosa)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oid_parametro", nullable = false)
    private ParametrosClinicos parametro;

    @Column(name = "ovalor_obtenido", nullable = false, precision = 10, scale = 2)
    private BigDecimal ovalorObtenido;

    @Column(name = "oes_anormal")
    private Boolean oesAnormal; // Nuestro backend calculará esto comparándolo con RangosReferencia
}