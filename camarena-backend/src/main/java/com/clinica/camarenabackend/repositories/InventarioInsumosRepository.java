package com.clinica.camarenabackend.repositories;


import com.clinica.camarenabackend.models.entities.InventarioInsumos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventarioInsumosRepository extends JpaRepository<InventarioInsumos, Integer> {
}
