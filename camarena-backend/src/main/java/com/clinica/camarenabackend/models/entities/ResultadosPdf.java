package com.clinica.camarenabackend.models.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "resultados_pdf")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResultadosPdf {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID oid_pdf;

    // Relación: Un PDF consolida toda una orden de laboratorio
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oid_orden", nullable = false)
    private OrdenLaboratorio orden;

    // Relación: Firma digital del biólogo responsable
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oid_biologo", nullable = false)
    private Empleado biologo;

    @Column(name = "opdf_url", nullable = false, length = 255)
    private String opdfUrl; // Ruta en S3 o almacenamiento local

    @Column(name = "estado", length = 50)
    private String estado;

    @Column(name = "fecha_aprobacion", nullable = false)
    private LocalDateTime fechaAprobacion;
}