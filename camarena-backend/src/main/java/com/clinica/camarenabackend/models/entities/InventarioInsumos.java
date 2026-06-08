package com.clinica.camarenabackend.models.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "inventario_insumos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventarioInsumos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer oid_insumo;

    @Column(name = "ocodigo_lote", length = 50)
    private String ocodigoLote;

    @Column(name = "onombre_insumo", nullable = false, length = 100)
    private String onombreInsumo; // Ej. Tubo rojo, Reactivo Glucosa

    @Column(name = "ostock_actual", nullable = false)
    private Integer ostockActual;

    @Column(name = "ostock_minimo", nullable = false)
    private Integer ostockMinimo;

    @Column(name = "fecha_caducidad")
    private LocalDate fechaCaducidad;
}
