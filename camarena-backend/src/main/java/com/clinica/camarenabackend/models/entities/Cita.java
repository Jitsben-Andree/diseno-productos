package com.clinica.camarenabackend.models.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "citas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID oid_cita;

    // Relación: Una cita pertenece a un paciente
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oid_paciente", nullable = false)
    private Paciente paciente;

    @Column(name = "ofecha_hora", nullable = false)
    private LocalDateTime ofechaHora;

    @Column(name = "oestado", length = 20)
    private String oestado; // Ej. Confirmada, Pendiente, Cancelada
}