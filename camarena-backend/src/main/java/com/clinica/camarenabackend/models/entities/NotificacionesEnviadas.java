package com.clinica.camarenabackend.models.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notificaciones_enviadas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificacionesEnviadas {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID oid_notificacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oid_paciente", nullable = false)
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_orden")
    private OrdenLaboratorio orden;

    @Column(name = "ocanal", nullable = false, length = 20)
    private String ocanal; // WHATSAPP, EMAIL, SMS

    @Column(name = "oestado_envio", nullable = false, length = 20)
    private String oestadoEnvio; // ENVIADO, FALLIDO

    @Column(name = "ofecha_envio", nullable = false)
    private LocalDateTime ofechaEnvio;
}