package com.ventastech.catalogo.repository;

import com.ventastech.catalogo.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByActivoTrue();
    Optional<Producto> findByCodigoSkuIgnoreCase(String codigoSku);
    boolean existsByCodigoSkuIgnoreCase(String codigoSku);
    boolean existsByNombreIgnoreCase(String nombre);

    List<Producto> findByCategoriaIdAndActivoTrue(Long categoriaId);
    List<Producto> findByMarcaIdAndActivoTrue(Long marcaId);
    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    @Query("SELECT p FROM Producto p WHERE p.precio BETWEEN :min AND :max AND p.activo = true")
    List<Producto> findByRangoPrecio(@Param("min") BigDecimal min, @Param("max") BigDecimal max);

    @Query("SELECT p FROM Producto p JOIN p.inventario i WHERE i.stockActual <= i.stockMinimo AND p.activo = true")
    List<Producto> findProductosBajoCritico();
}