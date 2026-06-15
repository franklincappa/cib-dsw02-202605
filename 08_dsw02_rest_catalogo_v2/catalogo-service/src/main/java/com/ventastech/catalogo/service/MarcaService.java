package com.ventastech.catalogo.service;

import com.ventastech.catalogo.dto.MarcaDTO;
import java.util.List;

public interface MarcaService {
    List<MarcaDTO> listarTodas();
    List<MarcaDTO> listarActivas();
    MarcaDTO obtenerPorId(Long id);
    MarcaDTO crear(MarcaDTO dto);
    MarcaDTO actualizar(Long id, MarcaDTO dto);
    void eliminar(Long id);
    void desactivar(Long id);
    List<MarcaDTO> listarPorPais(String pais);
}