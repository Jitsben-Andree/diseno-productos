package com.clinica.camarenabackend.models.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ordenes_laboratorio")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdenLaboratorio {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID oid_orden;

    // Relación: Una orden pertenece a un paciente
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oid_paciente", nullable = false)
    private Paciente paciente;

    // Relación Opcional: Una orden puede venir de una cita previa
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cita")
    private Cita cita;

    @Column(name = "ocodigo_ticket", nullable = false, length = 20, unique = true)
    private String ocodigoTicket;

    @Column(name = "ofecha_emision", nullable = false)
    private LocalDateTime ofechaEmision;

    @Column(name = "oestado_general", length = 20)
    private String oestadoGeneral; // Ej. Pendiente, En Proceso, Finalizado
}
