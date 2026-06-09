package com.ventastech.catalogo.controller;

import com.ventastech.catalogo.dto.*;
import com.ventastech.catalogo.service.ProductoService;
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
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductoDTO>>> listarTodos() {
        return ResponseEntity.ok(ApiResponse.ok(productoService.listarTodos()));
    }

    @GetMapping("/activos")
    public ResponseEntity<ApiResponse<List<ProductoDTO>>> listarActivos() {
        return ResponseEntity.ok(ApiResponse.ok(productoService.listarActivos()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductoDTO>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(productoService.obtenerPorId(id)));
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<ApiResponse<ProductoDTO>> obtenerPorSku(@PathVariable String sku) {
        return ResponseEntity.ok(ApiResponse.ok(productoService.obtenerPorSku(sku)));
    }

    @GetMapping("/buscar")
    public ResponseEntity<ApiResponse<List<ProductoDTO>>> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(ApiResponse.ok(productoService.buscarPorNombre(nombre)));
    }

    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<ApiResponse<List<ProductoDTO>>> listarPorCategoria(@PathVariable Long categoriaId) {
        return ResponseEntity.ok(ApiResponse.ok(productoService.listarPorCategoria(categoriaId)));
    }

    @GetMapping("/marca/{marcaId}")
    public ResponseEntity<ApiResponse<List<ProductoDTO>>> listarPorMarca(@PathVariable Long marcaId) {
        return ResponseEntity.ok(ApiResponse.ok(productoService.listarPorMarca(marcaId)));
    }

    @GetMapping("/precio")
    public ResponseEntity<ApiResponse<List<ProductoDTO>>> buscarPorPrecio(
            @RequestParam BigDecimal min, @RequestParam BigDecimal max) {
        return ResponseEntity.ok(ApiResponse.ok(productoService.buscarPorRangoPrecio(min, max)));
    }

    @GetMapping("/bajo-critico")
    public ResponseEntity<ApiResponse<List<ProductoDTO>>> listarBajoCritico() {
        return ResponseEntity.ok(ApiResponse.ok(productoService.listarBajoCritico()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductoDTO>> crear(@Valid @RequestBody ProductoDTO dto) {
        ProductoDTO creado = productoService.crear(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creado.getId()).toUri();
        return ResponseEntity.created(location).body(ApiResponse.ok("Producto creado", creado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductoDTO>> actualizar(
            @PathVariable Long id, @Valid @RequestBody ProductoDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok("Producto actualizado", productoService.actualizar(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.ok("Producto eliminado", null));
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        productoService.desactivar(id);
        return ResponseEntity.ok(ApiResponse.ok("Producto desactivado", null));
    }
}