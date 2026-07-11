package com.clinica.camarenabackend.repositories;


import com.clinica.camarenabackend.models.entities.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface PagoRepository extends JpaRepository<Pago, UUID> {
    List<Pago> findByFechaPagoBetween(LocalDateTime inicio, LocalDateTime fin);


    @Query(value = """
            SELECT DATE(fecha_pago) as dia, SUM(omonto_total) as total 
            FROM pagos 
            WHERE fecha_pago >= :fechaInicio 
            GROUP BY DATE(fecha_pago) 
            ORDER BY dia ASC
            """, nativeQuery = true)
    List<Object[]> agruparIngresosPorDia(@Param("fechaInicio") LocalDateTime fechaInicio);
}