package com.ventastech.catalogo.controller;

import com.ventastech.catalogo.dto.*;
import com.ventastech.catalogo.service.InventarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/inventario")
@RequiredArgsConstructor
public class InventarioController {

    private final InventarioService inventarioService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<InventarioDTO>>> listarTodo() {
        return ResponseEntity.ok(ApiResponse.ok(inventarioService.listarTodo()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InventarioDTO>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(inventarioService.obtenerPorId(id)));
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<ApiResponse<InventarioDTO>> obtenerPorProducto(@PathVariable Long productoId) {
        return ResponseEntity.ok(ApiResponse.ok(inventarioService.obtenerPorProducto(productoId)));
    }

    @GetMapping("/bajo-critico")
    public ResponseEntity<ApiResponse<List<InventarioDTO>>> listarBajoCritico() {
        return ResponseEntity.ok(ApiResponse.ok(inventarioService.listarBajoCritico()));
    }

    @GetMapping("/sin-stock")
    public ResponseEntity<ApiResponse<List<InventarioDTO>>> listarSinStock() {
        return ResponseEntity.ok(ApiResponse.ok(inventarioService.listarSinStock()));
    }

    @PutMapping("/producto/{productoId}")
    public ResponseEntity<ApiResponse<InventarioDTO>> actualizar(
            @PathVariable Long productoId, @RequestBody InventarioDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok("Inventario actualizado",
                inventarioService.actualizar(productoId, dto)));
    }

    @PatchMapping("/producto/{productoId}/ajustar")
    public ResponseEntity<ApiResponse<InventarioDTO>> ajustarStock(
            @PathVariable Long productoId,
            @RequestParam Integer cantidad) {
        return ResponseEntity.ok(ApiResponse.ok("Stock ajustado",
                inventarioService.ajustarStock(productoId, cantidad)));
    }
}