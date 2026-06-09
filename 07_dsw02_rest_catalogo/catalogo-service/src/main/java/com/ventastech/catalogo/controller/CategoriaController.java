package com.ventastech.catalogo.controller;

import com.ventastech.catalogo.dto.*;
import com.ventastech.catalogo.service.CategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoriaDTO>>> listarTodas() {
        return ResponseEntity.ok(ApiResponse.ok(categoriaService.listarTodas()));
    }

    @GetMapping("/activas")
    public ResponseEntity<ApiResponse<List<CategoriaDTO>>> listarActivas() {
        return ResponseEntity.ok(ApiResponse.ok(categoriaService.listarActivas()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoriaDTO>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(categoriaService.obtenerPorId(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CategoriaDTO>> crear(@Valid @RequestBody CategoriaDTO dto) {
        CategoriaDTO creada = categoriaService.crear(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creada.getId()).toUri();
        return ResponseEntity.created(location).body(ApiResponse.ok("Categoría creada", creada));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoriaDTO>> actualizar(
            @PathVariable Long id, @Valid @RequestBody CategoriaDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok("Categoría actualizada", categoriaService.actualizar(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        categoriaService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.ok("Categoría eliminada", null));
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        categoriaService.desactivar(id);
        return ResponseEntity.ok(ApiResponse.ok("Categoría desactivada", null));
    }
}
