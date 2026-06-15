package com.ventastech.catalogo.controller;

import com.ventastech.catalogo.dto.*;
import com.ventastech.catalogo.service.InventarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/inventario")
@RequiredArgsConstructor
@Tag(name = "Inventario", description = "Gestión de stock por producto")
public class InventarioController {

    private final InventarioService inventarioService;

    @GetMapping
    @Operation(summary = "Listar todo el inventario", description = "Cacheado 2 minutos.")
    public ResponseEntity<ApiResponse<List<InventarioDTO>>> listarTodo() {
        return ResponseEntity.ok(ApiResponse.ok(inventarioService.listarTodo()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener inventario por ID")
    public ResponseEntity<ApiResponse<InventarioDTO>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(inventarioService.obtenerPorId(id)));
    }

    @GetMapping("/producto/{productoId}")
    @Operation(summary = "Obtener inventario de un producto específico")
    public ResponseEntity<ApiResponse<InventarioDTO>> obtenerPorProducto(
            @Parameter(description = "ID del producto", example = "1") @PathVariable Long productoId) {
        return ResponseEntity.ok(ApiResponse.ok(inventarioService.obtenerPorProducto(productoId)));
    }

    @GetMapping("/bajo-critico")
    @Operation(summary = "Productos con stock <= mínimo", description = "Cacheado como parte de inventario.")
    public ResponseEntity<ApiResponse<List<InventarioDTO>>> listarBajoCritico() {
        return ResponseEntity.ok(ApiResponse.ok(inventarioService.listarBajoCritico()));
    }

    @GetMapping("/sin-stock")
    @Operation(summary = "Productos con stock = 0")
    public ResponseEntity<ApiResponse<List<InventarioDTO>>> listarSinStock() {
        return ResponseEntity.ok(ApiResponse.ok(inventarioService.listarSinStock()));
    }

    @PutMapping("/producto/{productoId}")
    @Operation(summary = "Actualizar límites de stock",
            description = "Actualiza stockMinimo y stockMaximo. Invalida caché de inventario y productos.")
    public ResponseEntity<ApiResponse<InventarioDTO>> actualizar(
            @PathVariable Long productoId, @RequestBody InventarioDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok("Inventario actualizado",
                inventarioService.actualizar(productoId, dto)));
    }

    @PatchMapping("/producto/{productoId}/ajustar")
    @Operation(summary = "Ajustar stock (entrada o salida)",
            description = "cantidad positiva = entrada. cantidad negativa = salida. Invalida caché.")
    public ResponseEntity<ApiResponse<InventarioDTO>> ajustarStock(
            @PathVariable Long productoId,
            @Parameter(description = "Cantidad a sumar o restar", example = "5") @RequestParam Integer cantidad) {
        return ResponseEntity.ok(ApiResponse.ok("Stock ajustado",
                inventarioService.ajustarStock(productoId, cantidad)));
    }
}