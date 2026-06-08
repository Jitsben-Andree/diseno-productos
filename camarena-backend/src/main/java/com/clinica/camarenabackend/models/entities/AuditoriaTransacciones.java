package com.clinica.camarenabackend.models.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "auditoria_transacciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditoriaTransacciones {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID oid_auditoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oid_usuario_empleado", nullable = false)
    private Empleado empleado;

    @Column(name = "otabla_afectada", nullable = false, length = 50)
    private String otablaAfectada; // Ej. resultados_datos

    @Column(name = "oaccion", nullable = false, length = 20)
    private String oaccion; // INSERT, UPDATE, DELETE

    @Column(name = "odireccion_ip", length = 45)
    private String odireccionIp;

    @Column(name = "ofecha_evento", nullable = false)
    private LocalDateTime ofechaEvento;

    // JSONB es excelente en PostgreSQL para guardar el rastro de cómo estaba el dato antes y cómo quedó después
    @Column(name = "datos_previos", columnDefinition = "jsonb")
    private String datosPrevios;

    @Column(name = "datos_nuevos", columnDefinition = "jsonb")
    private String datosNuevos;
}