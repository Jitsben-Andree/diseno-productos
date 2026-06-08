package com.clinica.camarenabackend.models.entities;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "examen_insumo")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamenInsumo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer oid_receta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oid_examen", nullable = false)
    private CatalogoExamenes examen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oid_insumo", nullable = false)
    private InventarioInsumos insumo;

    @Column(name = "ocantidad_requerida", nullable = false, precision = 10, scale = 2)
    private BigDecimal ocantidadRequerida;

    @Column(name = "ounidad_medida", length = 20)
    private String ounidadMedida; // Ej. ml, mg, unidad
}