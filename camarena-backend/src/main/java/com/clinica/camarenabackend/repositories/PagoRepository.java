package com.clinica.camarenabackend.repositories;


import com.clinica.camarenabackend.models.entities.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface PagoRepository extends JpaRepository<Pago, UUID> {
    List<Pago> findByFechaPagoBetween(LocalDateTime inicio, LocalDateTime fin);

}