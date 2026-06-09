package com.ventastech.catalogo.service.impl;

import com.ventastech.catalogo.dto.InventarioDTO;
import com.ventastech.catalogo.exception.ResourceNotFoundException;
import com.ventastech.catalogo.model.Inventario;
import com.ventastech.catalogo.repository.InventarioRepository;
import com.ventastech.catalogo.service.InventarioService;
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
public class InventarioServiceImpl implements InventarioService {

    private final InventarioRepository inventarioRepository;

    private InventarioDTO toDTO(Inventario i) {
        return InventarioDTO.builder()
                .id(i.getId())
                .productoId(i.getProducto().getId())
                .productoNombre(i.getProducto().getNombre())
                .productoSku(i.getProducto().getCodigoSku())
                .stockActual(i.getStockActual())
                .stockMinimo(i.getStockMinimo())
                .stockMaximo(i.getStockMaximo())
                .bajoCritico(i.getStockActual() <= i.getStockMinimo())
                .build();
    }

    @Override
    public List<InventarioDTO> listarTodo() {
        return inventarioRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public InventarioDTO obtenerPorId(Long id) {
        return toDTO(inventarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventario no encontrado con id: " + id)));
    }

    @Override
    public InventarioDTO obtenerPorProducto(Long productoId) {
        return toDTO(inventarioRepository.findByProductoId(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventario no encontrado para producto id: " + productoId)));
    }

    @Override
    @Transactional
    public InventarioDTO actualizar(Long productoId, InventarioDTO dto) {
        Inventario inv = inventarioRepository.findByProductoId(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventario no encontrado para producto id: " + productoId));
        if (dto.getStockMinimo() != null) inv.setStockMinimo(dto.getStockMinimo());
        if (dto.getStockMaximo() != null) inv.setStockMaximo(dto.getStockMaximo());
        if (dto.getStockActual() != null) inv.setStockActual(dto.getStockActual());
        return toDTO(inventarioRepository.save(inv));
    }

    @Override
    @Transactional
    public InventarioDTO ajustarStock(Long productoId, Integer cantidad) {
        Inventario inv = inventarioRepository.findByProductoId(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventario no encontrado para producto id: " + productoId));
        int nuevo = inv.getStockActual() + cantidad;
        if (nuevo < 0)
            throw new IllegalArgumentException("Stock insuficiente. Disponible: " + inv.getStockActual());
        if (nuevo > inv.getStockMaximo())
            throw new IllegalArgumentException("El ajuste supera el stock máximo permitido: " + inv.getStockMaximo());
        inv.setStockActual(nuevo);
        log.info("Stock ajustado — productoId: {}, cantidad: {}, nuevo stock: {}", productoId, cantidad, nuevo);
        return toDTO(inventarioRepository.save(inv));
    }

    @Override
    public List<InventarioDTO> listarBajoCritico() {
        return inventarioRepository.findBajoCritico().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<InventarioDTO> listarSinStock() {
        return inventarioRepository.findSinStock().stream().map(this::toDTO).collect(Collectors.toList());
    }
}