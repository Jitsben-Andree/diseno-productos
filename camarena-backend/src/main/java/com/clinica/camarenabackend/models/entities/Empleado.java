package com.clinica.camarenabackend.models.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "empleados")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Empleado {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID oid_empleado;

    // Relación 1 a 1: Un empleado tiene un único usuario de sistema
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oid_usuario", referencedColumnName = "oid_usuario", unique = true, nullable = false)
    private Usuario usuario;

    @Column(name = "odni", nullable = false, length = 15, unique = true)
    private String odni;

    @Column(name = "onombres", nullable = false, length = 100)
    private String onombres;

    @Column(name = "oapellidos", nullable = false, length = 100)
    private String oapellidos;

    @Column(name = "cmp_colegiatura", length = 20)
    private String cmpColegiatura;

    @Column(name = "cargo", length = 50)
    private String cargo;
}