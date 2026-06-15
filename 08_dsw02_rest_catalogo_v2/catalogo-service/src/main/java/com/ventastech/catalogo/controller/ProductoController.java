package com.ventastech.catalogo.controller;

import com.ventastech.catalogo.dto.*;
import com.ventastech.catalogo.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "Gestión del catálogo de productos")
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping
    @Operation(summary = "Listar todos los productos", description = "Cacheado 5 minutos.")
    public ResponseEntity<ApiResponse<List<ProductoDTO>>> listarTodos() {
        return ResponseEntity.ok(ApiResponse.ok(productoService.listarTodos()));
    }

    @GetMapping("/activos")
    @Operation(summary = "Listar productos activos", description = "Cacheado 5 minutos.")
    public ResponseEntity<ApiResponse<List<ProductoDTO>>> listarActivos() {
        return ResponseEntity.ok(ApiResponse.ok(productoService.listarActivos()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por ID", description = "Cacheado 5 minutos por ID.")
    public ResponseEntity<ApiResponse<ProductoDTO>> obtenerPorId(
            @Parameter(description = "ID del producto", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(productoService.obtenerPorId(id)));
    }

    @GetMapping("/sku/{sku}")
    @Operation(summary = "Obtener producto por código SKU")
    public ResponseEntity<ApiResponse<ProductoDTO>> obtenerPorSku(
            @Parameter(description = "Código SKU", example = "LAP-DELL-001") @PathVariable String sku) {
        return ResponseEntity.ok(ApiResponse.ok(productoService.obtenerPorSku(sku)));
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar productos por nombre",
            description = "Búsqueda parcial. No se cachea — parámetro muy variable.")
    public ResponseEntity<ApiResponse<List<ProductoDTO>>> buscarPorNombre(
            @Parameter(description = "Texto a buscar", example = "Dell") @RequestParam String nombre) {
        return ResponseEntity.ok(ApiResponse.ok(productoService.buscarPorNombre(nombre)));
    }

    @GetMapping("/categoria/{categoriaId}")
    @Operation(summary = "Listar productos por categoría", description = "Cacheado 5 minutos por categoría.")
    public ResponseEntity<ApiResponse<List<ProductoDTO>>> listarPorCategoria(
            @Parameter(description = "ID de la categoría", example = "1") @PathVariable Long categoriaId) {
        return ResponseEntity.ok(ApiResponse.ok(productoService.listarPorCategoria(categoriaId)));
    }

    @GetMapping("/marca/{marcaId}")
    @Operation(summary = "Listar productos por marca", description = "Cacheado 5 minutos por marca.")
    public ResponseEntity<ApiResponse<List<ProductoDTO>>> listarPorMarca(
            @Parameter(description = "ID de la marca", example = "1") @PathVariable Long marcaId) {
        return ResponseEntity.ok(ApiResponse.ok(productoService.listarPorMarca(marcaId)));
    }

    @GetMapping("/precio")
    @Operation(summary = "Filtrar por rango de precio",
            description = "No se cachea — rango varía por petición.")
    public ResponseEntity<ApiResponse<List<ProductoDTO>>> buscarPorPrecio(
            @Parameter(example = "100") @RequestParam BigDecimal min,
            @Parameter(example = "1000") @RequestParam BigDecimal max) {
        return ResponseEntity.ok(ApiResponse.ok(productoService.buscarPorRangoPrecio(min, max)));
    }

    @GetMapping("/bajo-critico")
    @Operation(summary = "Productos con stock bajo el mínimo", description = "Cacheado 1 minuto.")
    public ResponseEntity<ApiResponse<List<ProductoDTO>>> listarBajoCritico() {
        return ResponseEntity.ok(ApiResponse.ok(productoService.listarBajoCritico()));
    }

    @PostMapping
    @Operation(summary = "Crear producto",
            description = "Crea el producto y genera su inventario inicial. Invalida toda la caché de productos.")
    public ResponseEntity<ApiResponse<ProductoDTO>> crear(@Valid @RequestBody ProductoDTO dto) {
        ProductoDTO creado = productoService.crear(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creado.getId()).toUri();
        return ResponseEntity.created(location).body(ApiResponse.ok("Producto creado", creado));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar producto completo", description = "Invalida caché individual y listas.")
    public ResponseEntity<ApiResponse<ProductoDTO>> actualizar(
            @PathVariable Long id, @Valid @RequestBody ProductoDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok("Producto actualizado", productoService.actualizar(id, dto)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar producto (físico)")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.ok("Producto eliminado", null));
    }

    @PatchMapping("/{id}/desactivar")
    @Operation(summary = "Desactivar producto (borrado lógico)")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        productoService.desactivar(id);
        return ResponseEntity.ok(ApiResponse.ok("Producto desactivado", null));
    }
}