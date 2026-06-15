package com.ventastech.catalogo.service.impl;

import com.ventastech.catalogo.dto.MarcaDTO;
import com.ventastech.catalogo.exception.ResourceNotFoundException;
import com.ventastech.catalogo.model.Marca;
import com.ventastech.catalogo.repository.MarcaRepository;
import com.ventastech.catalogo.service.MarcaService;
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
public class MarcaServiceImpl implements MarcaService {

    private final MarcaRepository marcaRepository;

    private MarcaDTO toDTO(Marca m) {
        return MarcaDTO.builder()
                .id(m.getId()).nombre(m.getNombre())
                .descripcion(m.getDescripcion())
                .paisOrigen(m.getPaisOrigen())
                .activo(m.getActivo())
                .build();
    }

    private Marca toEntity(MarcaDTO dto) {
        return Marca.builder()
                .nombre(dto.getNombre()).descripcion(dto.getDescripcion())
                .paisOrigen(dto.getPaisOrigen())
                .activo(dto.getActivo() != null ? dto.getActivo() : true)
                .build();
    }

    @Override
    @Cacheable(value = "marcas", key = "'todas'")
    public List<MarcaDTO> listarTodas() {
        log.info("[CACHE MISS] listarTodas marcas — consultando BD");
        return marcaRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "marcas", key = "'activas'")
    public List<MarcaDTO> listarActivas() {
        return marcaRepository.findByActivoTrue().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "marca", key = "#id")
    public MarcaDTO obtenerPorId(Long id) {
        log.info("[CACHE MISS] obtenerMarca id={} — consultando BD", id);
        return toDTO(marcaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Marca no encontrada con id: " + id)));
    }

    @Override
    @Transactional
    @CacheEvict(value = {"marcas", "marca"}, allEntries = true)
    public MarcaDTO crear(MarcaDTO dto) {
        if (marcaRepository.existsByNombreIgnoreCase(dto.getNombre()))
            throw new IllegalArgumentException("Ya existe una marca con el nombre: " + dto.getNombre());
        Marca guardada = marcaRepository.save(toEntity(dto));
        log.info("[CACHE EVICT] marcas — nueva marca id={}", guardada.getId());
        return toDTO(guardada);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "marcas", allEntries = true),
            @CacheEvict(value = "marca",  key = "#id")
    })
    public MarcaDTO actualizar(Long id, MarcaDTO dto) {
        Marca m = marcaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Marca no encontrada con id: " + id));
        m.setNombre(dto.getNombre()); m.setDescripcion(dto.getDescripcion());
        m.setPaisOrigen(dto.getPaisOrigen()); m.setActivo(dto.getActivo());
        return toDTO(marcaRepository.save(m));
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "marcas", allEntries = true),
            @CacheEvict(value = "marca",  key = "#id")
    })
    public void eliminar(Long id) {
        if (!marcaRepository.existsById(id))
            throw new ResourceNotFoundException("Marca no encontrada con id: " + id);
        marcaRepository.deleteById(id);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "marcas", allEntries = true),
            @CacheEvict(value = "marca",  key = "#id")
    })
    public void desactivar(Long id) {
        Marca m = marcaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Marca no encontrada con id: " + id));
        m.setActivo(false);
        marcaRepository.save(m);
    }

    @Override
    @Cacheable(value = "marcas", key = "'pais-' + #pais.toLowerCase()")
    public List<MarcaDTO> listarPorPais(String pais) {
        return marcaRepository.findByPaisOrigenIgnoreCase(pais)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

}