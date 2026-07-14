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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oid_orden", nullable = false)
    private OrdenLaboratorio orden;

    // Mantenemos esto estricto (nullable = false). El Seeder ya soluciona el problema.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oid_biologo", nullable = false)
    private Empleado biologo;

    @Column(name = "opdf_url", nullable = false, length = 255)
    private String opdfUrl;

    @Column(name = "estado", length = 50)
    private String estado;

    @Column(name = "fecha_aprobacion", nullable = false)
    private LocalDateTime fechaAprobacion;
}