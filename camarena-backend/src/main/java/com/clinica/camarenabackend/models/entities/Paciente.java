package com.clinica.camarenabackend.models.entities;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "pacientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID oid_paciente;

    // Relación Muchos a Uno: Gestión de perfiles familiares (Un usuario web administra varios pacientes)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oid_usuario")
    private Usuario usuario;

    @Column(name = "odni", nullable = false, length = 15, unique = true)
    private String odni;

    @Column(name = "onombres", nullable = false, length = 100)
    private String onombres;

    @Column(name = "oapellidos", nullable = false, length = 100)
    private String oapellidos;

    @Column(name = "ofecha_nacimiento", nullable = false)
    private LocalDate ofechaNacimiento;

    // Campo crítico para el motor de inferencias (Rangos de Referencia)
    @Column(name = "osexo", nullable = false, length = 1)
    private String osexo; // 'M' o 'F'

    @Column(name = "telefono", length = 15)
    private String telefono;

    @Column(name = "ovalidado_reniec", nullable = false)
    private Boolean ovalidadoReniec = false;
}