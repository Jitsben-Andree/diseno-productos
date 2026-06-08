package com.clinica.camarenabackend.models.entities;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;


@Entity
@Table(name = "rangos_referencia")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RangosReferencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer oid_rango;

    // Relación: Un parámetro tiene distintos rangos según la demografía del paciente
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oid_parametro", nullable = false)
    private ParametrosClinicos parametro;

    @Column(name = "osexo_aplica", nullable = false, length = 1)
    private String osexoAplica; // 'M' para Masculino, 'F' para Femenino, 'A' para Ambos

    @Column(name = "oedad_min_anios", nullable = false)
    private Integer oedadMinAnios;

    @Column(name = "oedad_max_anios", nullable = false)
    private Integer oedadMaxAnios;

    @Column(name = "ovalor_min", nullable = false, precision = 10, scale = 2)
    private BigDecimal ovalorMin;

    @Column(name = "ovalor_max", nullable = false, precision = 10, scale = 2)
    private BigDecimal ovalorMax;
}
