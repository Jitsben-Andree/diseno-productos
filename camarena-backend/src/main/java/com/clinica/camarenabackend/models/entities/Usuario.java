package com.clinica.camarenabackend.models.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID oid_usuario;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "oid_rol", nullable = false)
    private Rol rol;

    @Column(nullable = false, length = 100, unique = true)
    private String email;

    @Column(nullable = false, length = 255)
    private String opassword_hash;

    @Column(nullable = false)
    private Boolean oactivo = true;
}