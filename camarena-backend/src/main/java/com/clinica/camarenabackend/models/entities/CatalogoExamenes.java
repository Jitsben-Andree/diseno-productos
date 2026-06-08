package com.clinica.camarenabackend.models.entities;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "catalogo_examenes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CatalogoExamenes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer oid_examen;

    @Column(name = "ocodigo", nullable = false, length = 20, unique = true)
    private String ocodigo;

    @Column(name = "odescripcion", nullable = false, length = 150)
    private String odescripcion;

    @Column(name = "otipo_tubo_defecto", nullable = false, length = 50)
    private String otipoTuboDefecto;

    @Column(name = "oprecio_base", nullable = false, precision = 10, scale = 2)
    private BigDecimal oprecioBase;
}