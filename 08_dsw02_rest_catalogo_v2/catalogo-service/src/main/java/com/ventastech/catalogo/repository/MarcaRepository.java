package com.ventastech.catalogo.repository;

import com.ventastech.catalogo.model.Marca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MarcaRepository extends JpaRepository<Marca, Long> {
    List<Marca> findByActivoTrue();
    Optional<Marca> findByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCase(String nombre);
    List<Marca> findByPaisOrigenIgnoreCase(String paisOrigen);
}