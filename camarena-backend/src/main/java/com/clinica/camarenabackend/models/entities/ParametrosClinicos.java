package com.clinica.camarenabackend.models.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "parametros_clinicos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParametrosClinicos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer oid_parametro;

    // Relación: Muchos parámetros pertenecen a un examen (Ej. Glucosa pertenece a Hemograma)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oid_examen", nullable = false)
    private CatalogoExamenes examen;

    @Column(name = "onombre", nullable = false, length = 100)
    private String onombre;

    @Column(name = "unidad", length = 20)
    private String unidad; // Ej. mg/dL, g/L
}