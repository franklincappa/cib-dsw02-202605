package com.ventastech.catalogo.controller;

import com.ventastech.catalogo.dto.*;
import com.ventastech.catalogo.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Categorías", description = "Gestión de categorías de productos")
public class CategoriaController {

    private final CategoriaService categoriaService;

    @GetMapping
    @Operation(summary = "Listar todas las categorías",
            description = "Retorna todas las categorías. Resultado cacheado 30 minutos.")
    public ResponseEntity<ApiResponse<List<CategoriaDTO>>> listarTodas() {
        return ResponseEntity.ok(ApiResponse.ok(categoriaService.listarTodas()));
    }

    @GetMapping("/activas")
    @Operation(summary = "Listar categorías activas",
            description = "Retorna solo categorías con activo=true. Cacheado 30 minutos.")
    public ResponseEntity<ApiResponse<List<CategoriaDTO>>> listarActivas() {
        return ResponseEntity.ok(ApiResponse.ok(categoriaService.listarActivas()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener Categoría por ID")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Categoría encontrada"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    public ResponseEntity<ApiResponse<CategoriaDTO>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(categoriaService.obtenerPorId(id)));
    }

    @PostMapping
    @Operation(summary = "Crear categoría",
            description = "Crea una nueva categoría e invalida el caché de categorías.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Categoría creada"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos inválidos o nombre duplicado")
    })
    public ResponseEntity<ApiResponse<CategoriaDTO>> crear(@Valid @RequestBody CategoriaDTO dto) {
        CategoriaDTO creada = categoriaService.crear(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creada.getId()).toUri();
        return ResponseEntity.created(location).body(ApiResponse.ok("Categoría creada", creada));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar categoría completa",
            description = "Actualiza todos los campos e invalida caché individual y lista.")
    public ResponseEntity<ApiResponse<CategoriaDTO>> actualizar(
            @PathVariable Long id, @Valid @RequestBody CategoriaDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok("Categoría actualizada", categoriaService.actualizar(id, dto)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar categoría (físico)")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        categoriaService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.ok("Categoría eliminada", null));
    }

    @PatchMapping("/{id}/desactivar")
    @Operation(summary = "Desactivar categoría (borrado lógico)",
            description = "Establece activo=false sin eliminar el registro.")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        categoriaService.desactivar(id);
        return ResponseEntity.ok(ApiResponse.ok("Categoría desactivada", null));
    }

}
