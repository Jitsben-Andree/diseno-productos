package com.clinica.camarenabackend.models.entities;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pagos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID oid_pago;

    // Relación: Varios pagos/abonos pueden pertenecer a una misma orden
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oid_orden", nullable = false)
    private OrdenLaboratorio orden;

    @Column(name = "omonto_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal omontoTotal;

    @Column(name = "ometodo_pago", length = 50)
    private String ometodoPago; // Ej. Efectivo, Tarjeta, Yape

    @Column(name = "nro_comprobante", length = 50)
    private String nroComprobante;

    @Column(name = "fecha_pago", nullable = false)
    private LocalDateTime fechaPago;
}