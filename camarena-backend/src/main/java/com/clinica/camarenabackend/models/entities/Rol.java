package com.clinica.camarenabackend.models.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer oid_rol;

    @Column(nullable = false, length = 50, unique = true)
    private String onombre_rol;
}