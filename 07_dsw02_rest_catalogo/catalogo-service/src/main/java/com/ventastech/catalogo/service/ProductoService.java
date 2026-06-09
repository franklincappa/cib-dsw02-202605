package com.ventastech.catalogo.service;

import com.ventastech.catalogo.dto.ProductoDTO;
import java.math.BigDecimal;
import java.util.List;

public interface ProductoService {
    List<ProductoDTO> listarTodos();
    List<ProductoDTO> listarActivos();
    ProductoDTO obtenerPorId(Long id);
    ProductoDTO obtenerPorSku(String sku);
    ProductoDTO crear(ProductoDTO dto);
    ProductoDTO actualizar(Long id, ProductoDTO dto);
    void eliminar(Long id);
    void desactivar(Long id);
    List<ProductoDTO> buscarPorNombre(String nombre);
    List<ProductoDTO> listarPorCategoria(Long categoriaId);
    List<ProductoDTO> listarPorMarca(Long marcaId);
    List<ProductoDTO> buscarPorRangoPrecio(BigDecimal min, BigDecimal max);
    List<ProductoDTO> listarBajoCritico();
}
