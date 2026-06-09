package com.ventastech.catalogo.repository;

import com.ventastech.catalogo.model.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long> {

    Optional<Inventario> findByProductoId(Long productoId);
    boolean existsByProductoId(Long productoId);

    @Query("SELECT i FROM Inventario i WHERE i.stockActual <= i.stockMinimo")
    List<Inventario> findBajoCritico();

    @Query("SELECT i FROM Inventario i WHERE i.stockActual = 0")
    List<Inventario> findSinStock();
}