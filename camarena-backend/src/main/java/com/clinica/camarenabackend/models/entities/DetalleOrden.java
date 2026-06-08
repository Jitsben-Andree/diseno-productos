package com.clinica.camarenabackend.models.entities;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "detalles_orden")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleOrden {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID oid_detalle;

    // Relación: Muchos detalles pertenecen a una sola orden
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oid_orden", nullable = false)
    private OrdenLaboratorio orden;

    // Relación: Cada detalle corresponde a un examen del catálogo
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oid_examen", nullable = false)
    private CatalogoExamenes examen;

    @Column(name = "oprecio_cobrado", nullable = false, precision = 10, scale = 2)
    private BigDecimal oprecioCobrado;

    @Column(name = "oestado_examen", length = 30)
    private String oestadoExamen;
}