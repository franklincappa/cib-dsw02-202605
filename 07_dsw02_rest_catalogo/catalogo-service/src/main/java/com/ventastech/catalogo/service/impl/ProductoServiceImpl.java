package com.ventastech.catalogo.service.impl;

import com.ventastech.catalogo.dto.ProductoDTO;
import com.ventastech.catalogo.exception.ResourceNotFoundException;
import com.ventastech.catalogo.model.*;
import com.ventastech.catalogo.repository.*;
import com.ventastech.catalogo.service.ProductoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository   productoRepository;
    private final CategoriaRepository  categoriaRepository;
    private final MarcaRepository      marcaRepository;

    private ProductoDTO toDTO(Producto p) {
        Inventario inv = p.getInventario();
        return ProductoDTO.builder()
                .id(p.getId())
                .categoriaId(p.getCategoria()  != null ? p.getCategoria().getId()     : null)
                .categoriaNombre(p.getCategoria() != null ? p.getCategoria().getNombre() : null)
                .marcaId(p.getMarca()     != null ? p.getMarca().getId()         : null)
                .marcaNombre(p.getMarca()    != null ? p.getMarca().getNombre()      : null)
                .codigoSku(p.getCodigoSku())
                .nombre(p.getNombre())
                .descripcion(p.getDescripcion())
                .precio(p.getPrecio())
                .activo(p.getActivo())
                .stockActual(inv != null ? inv.getStockActual() : null)
                .stockMinimo(inv != null ? inv.getStockMinimo() : null)
                .stockMaximo(inv != null ? inv.getStockMaximo() : null)
                .build();
    }

    private Producto toEntity(ProductoDTO dto) {
        return Producto.builder()
                .codigoSku(dto.getCodigoSku())
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .precio(dto.getPrecio())
                .activo(dto.getActivo() != null ? dto.getActivo() : true)
                .build();
    }

    private Categoria resolverCategoria(Long categoriaId) {
        if (categoriaId == null) return null;
        return categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + categoriaId));
    }

    private Marca resolverMarca(Long marcaId) {
        if (marcaId == null) return null;
        return marcaRepository.findById(marcaId)
                .orElseThrow(() -> new ResourceNotFoundException("Marca no encontrada con id: " + marcaId));
    }

    @Override
    public List<ProductoDTO> listarTodos() {
        return productoRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<ProductoDTO> listarActivos() {
        return productoRepository.findByActivoTrue().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public ProductoDTO obtenerPorId(Long id) {
        return toDTO(productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id)));
    }

    @Override
    public ProductoDTO obtenerPorSku(String sku) {
        return toDTO(productoRepository.findByCodigoSkuIgnoreCase(sku)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con SKU: " + sku)));
    }

    @Override
    @Transactional
    public ProductoDTO crear(ProductoDTO dto) {
        if (productoRepository.existsByCodigoSkuIgnoreCase(dto.getCodigoSku()))
            throw new IllegalArgumentException("Ya existe un producto con el SKU: " + dto.getCodigoSku());
        if (productoRepository.existsByNombreIgnoreCase(dto.getNombre()))
            throw new IllegalArgumentException("Ya existe un producto con el nombre: " + dto.getNombre());

        Producto nuevo = toEntity(dto);
        nuevo.setCategoria(resolverCategoria(dto.getCategoriaId()));
        nuevo.setMarca(resolverMarca(dto.getMarcaId()));

        Producto guardado = productoRepository.save(nuevo);

        // Crear inventario inicial automáticamente
        Inventario inventario = Inventario.builder()
                .producto(guardado)
                .stockActual(dto.getStockActual() != null ? dto.getStockActual() : 0)
                .stockMinimo(dto.getStockMinimo() != null ? dto.getStockMinimo() : 0)
                .stockMaximo(dto.getStockMaximo() != null ? dto.getStockMaximo() : 9999)
                .build();
        guardado.setInventario(inventario);

        log.info("Producto creado con id: {} y SKU: {}", guardado.getId(), guardado.getCodigoSku());
        return toDTO(productoRepository.save(guardado));
    }

    @Override
    @Transactional
    public ProductoDTO actualizar(Long id, ProductoDTO dto) {
        Producto p = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
        p.setNombre(dto.getNombre());
        p.setDescripcion(dto.getDescripcion());
        p.setPrecio(dto.getPrecio());
        p.setActivo(dto.getActivo());
        p.setCodigoSku(dto.getCodigoSku());
        p.setCategoria(resolverCategoria(dto.getCategoriaId()));
        p.setMarca(resolverMarca(dto.getMarcaId()));
        return toDTO(productoRepository.save(p));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!productoRepository.existsById(id))
            throw new ResourceNotFoundException("Producto no encontrado con id: " + id);
        productoRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void desactivar(Long id) {
        Producto p = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
        p.setActivo(false);
        productoRepository.save(p);
    }

    @Override
    public List<ProductoDTO> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<ProductoDTO> listarPorCategoria(Long categoriaId) {
        return productoRepository.findByCategoriaIdAndActivoTrue(categoriaId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<ProductoDTO> listarPorMarca(Long marcaId) {
        return productoRepository.findByMarcaIdAndActivoTrue(marcaId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<ProductoDTO> buscarPorRangoPrecio(BigDecimal min, BigDecimal max) {
        return productoRepository.findByRangoPrecio(min, max)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<ProductoDTO> listarBajoCritico() {
        return productoRepository.findProductosBajoCritico()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }
}
