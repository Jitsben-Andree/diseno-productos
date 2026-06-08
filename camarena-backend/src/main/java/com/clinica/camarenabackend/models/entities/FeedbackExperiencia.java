package com.clinica.camarenabackend.models.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "feedback_experiencia")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackExperiencia {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID oid_feedback;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oid_orden", nullable = false)
    private OrdenLaboratorio orden;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oid_paciente", nullable = false)
    private Paciente paciente;

    @Column(name = "ocsat_score", nullable = false)
    private Integer ocsatScore; // Puntuación de 1 a 5 estrellas

    @Column(name = "comentarios_ux", columnDefinition = "TEXT")
    private String comentariosUx;

    @Column(name = "fecha_calificacion", nullable = false)
    private LocalDateTime fechaCalificacion;
}