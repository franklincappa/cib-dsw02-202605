package com.ventastech.catalogo.service;

import com.ventastech.catalogo.dto.InventarioDTO;
import java.util.List;

public interface InventarioService {
    List<InventarioDTO> listarTodo();
    InventarioDTO obtenerPorId(Long id);
    InventarioDTO obtenerPorProducto(Long productoId);
    InventarioDTO actualizar(Long productoId, InventarioDTO dto);
    InventarioDTO ajustarStock(Long productoId, Integer cantidad);
    List<InventarioDTO> listarBajoCritico();
    List<InventarioDTO> listarSinStock();
}