package com.ventastech.catalogo.service.impl;

import com.ventastech.catalogo.dto.CategoriaDTO;
import com.ventastech.catalogo.exception.ResourceNotFoundException;
import com.ventastech.catalogo.model.Categoria;
import com.ventastech.catalogo.repository.CategoriaRepository;
import com.ventastech.catalogo.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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
    @Cacheable(value = "categorias", key = "'todas'")
    public List<CategoriaDTO> listarTodas() {
        log.info("[CACHE MISS] listarTodas categorías — consultando BD");
        return categoriaRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "categorias", key = "'activas'")
    public List<CategoriaDTO> listarActivas() {
        log.info("[CACHE MISS] listarActivas categorías — consultando BD");
        return categoriaRepository.findByActivoTrue()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "categoria", key = "#id")
    public CategoriaDTO obtenerPorId(Long id) {
        log.info("[CACHE MISS] obtenerCategoria id={} — consultando BD", id);
        return toDTO(categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + id)));
    }

    @Override
    @Transactional
    @CacheEvict(value = {"categorias", "categoria"}, allEntries = true)
    public CategoriaDTO crear(CategoriaDTO dto) {
        if (categoriaRepository.existsByNombreIgnoreCase(dto.getNombre()))
            throw new IllegalArgumentException("Ya existe una categoría con el nombre: " + dto.getNombre());
        Categoria guardada = categoriaRepository.save(toEntity(dto));
        log.info("[CACHE EVICT] categorias — nueva categoría id={}", guardada.getId());
        return toDTO(guardada);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "categorias", allEntries = true),
            @CacheEvict(value = "categoria",  key = "#id")
    })
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
    @Caching(evict = {
            @CacheEvict(value = "categorias", allEntries = true),
            @CacheEvict(value = "categoria",  key = "#id")
    })
    public void eliminar(Long id) {
        if (!categoriaRepository.existsById(id))
            throw new ResourceNotFoundException("Categoría no encontrada con id: " + id);
        categoriaRepository.deleteById(id);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "categorias", allEntries = true),
            @CacheEvict(value = "categoria",  key = "#id")
    })
    public void desactivar(Long id) {
        Categoria c = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + id));
        c.setActivo(false);
        categoriaRepository.save(c);
    }

}
