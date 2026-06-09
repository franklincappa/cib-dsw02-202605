package com.ventastech.catalogo.service.impl;

import com.ventastech.catalogo.dto.CategoriaDTO;
import com.ventastech.catalogo.exception.ResourceNotFoundException;
import com.ventastech.catalogo.model.Categoria;
import com.ventastech.catalogo.repository.CategoriaRepository;
import com.ventastech.catalogo.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;

    private CategoriaDTO toDTO(Categoria c) {
        return CategoriaDTO.builder()
                .id(c.getId()).nombre(c.getNombre())
                .descripcion(c.getDescripcion()).activo(c.getActivo())
                .build();
    }

    private Categoria toEntity(CategoriaDTO dto) {
        return Categoria.builder()
                .nombre(dto.getNombre()).descripcion(dto.getDescripcion())
                .activo(dto.getActivo() != null ? dto.getActivo() : true)
                .build();
    }

    @Override
    public List<CategoriaDTO> listarTodas() {
        return categoriaRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<CategoriaDTO> listarActivas() {
        return categoriaRepository.findByActivoTrue().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public CategoriaDTO obtenerPorId(Long id) {
        return toDTO(categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + id)));
    }

    @Override
    @Transactional
    public CategoriaDTO crear(CategoriaDTO dto) {
        if (categoriaRepository.existsByNombreIgnoreCase(dto.getNombre()))
            throw new IllegalArgumentException("Ya existe una categoría con el nombre: " + dto.getNombre());
        Categoria guardada = categoriaRepository.save(toEntity(dto));
        log.info("Categoría creada con id: {}", guardada.getId());
        return toDTO(guardada);
    }

    @Override
    @Transactional
    public CategoriaDTO actualizar(Long id, CategoriaDTO dto) {
        Categoria c = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + id));
        c.setNombre(dto.getNombre());
        c.setDescripcion(dto.getDescripcion());
        c.setActivo(dto.getActivo());
        return toDTO(categoriaRepository.save(c));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!categoriaRepository.existsById(id))
            throw new ResourceNotFoundException("Categoría no encontrada con id: " + id);
        categoriaRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void desactivar(Long id) {
        Categoria c = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + id));
        c.setActivo(false);
        categoriaRepository.save(c);
    }
}