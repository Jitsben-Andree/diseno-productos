package com.clinica.camarenabackend.models.entities;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "muestras_clinicas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MuestraClinica {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID oid_muestra;

    // Relación: Una muestra pertenece a un detalle específico (Ej. Tubo lila para Hemograma)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oid_detalle", nullable = false)
    private DetalleOrden detalleOrden;

    @Column(name = "ocodigo_barras", nullable = false, length = 50, unique = true)
    private String ocodigoBarras; // Ej. BAR-2026-0001

    @Column(name = "oestado_muestra", length = 30)
    private String oestadoMuestra; // Ej. Tomada, Procesando, Rechazada
}