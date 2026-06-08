package com.clinica.camarenabackend.models.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tokens_recuperacion")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokensRecuperacion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID oid_token;

    // Relación: Un usuario puede solicitar varios tokens de recuperación en el tiempo
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oid_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "otoken_hash", nullable = false, length = 255, unique = true)
    private String otokenHash;

    @Column(name = "ofecha_expiracion", nullable = false)
    private LocalDateTime ofechaExpiracion;

    @Column(name = "ousado", nullable = false)
    private Boolean ousado = false;
}
