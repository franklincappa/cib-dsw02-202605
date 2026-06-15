package com.ventastech.catalogo.service;

import com.ventastech.catalogo.dto.CategoriaDTO;
import java.util.List;

public interface CategoriaService {
    List<CategoriaDTO> listarTodas();
    List<CategoriaDTO> listarActivas();
    CategoriaDTO obtenerPorId(Long id);
    CategoriaDTO crear(CategoriaDTO dto);
    CategoriaDTO actualizar(Long id, CategoriaDTO dto);
    void eliminar(Long id);
    void desactivar(Long id);
}