package com.ventastech.catalogo.controller;

import com.ventastech.catalogo.dto.*;
import com.ventastech.catalogo.service.MarcaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/api/v1/marcas")
@RequiredArgsConstructor
@Tag(name = "Marcas", description = "Gestión de marcas de productos")
public class MarcaController {

    private final MarcaService marcaService;

    @GetMapping
    @Operation(summary = "Listar todas las marcas", description = "Cacheado 30 minutos.")
    public ResponseEntity<ApiResponse<List<MarcaDTO>>> listarTodas() {
        return ResponseEntity.ok(ApiResponse.ok(marcaService.listarTodas()));
    }

    @GetMapping("/activas")
    @Operation(summary = "Listar marcas activas")
    public ResponseEntity<ApiResponse<List<MarcaDTO>>> listarActivas() {
        return ResponseEntity.ok(ApiResponse.ok(marcaService.listarActivas()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener marca por ID")
    public ResponseEntity<ApiResponse<MarcaDTO>> obtenerPorId(
            @Parameter(description = "ID de la marca", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(marcaService.obtenerPorId(id)));
    }

    @GetMapping("/pais")
    @Operation(summary = "Listar marcas por país de origen",
            description = "Filtra marcas según su país. Cacheado por país.")
    public ResponseEntity<ApiResponse<List<MarcaDTO>>> listarPorPais(
            @Parameter(description = "País de origen", example = "Japón") @RequestParam String pais) {
        return ResponseEntity.ok(ApiResponse.ok(marcaService.listarPorPais(pais)));
    }

    @PostMapping
    @Operation(summary = "Crear marca", description = "Invalida el caché de marcas al crear.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Marca creada"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Nombre duplicado o datos inválidos")
    })
    public ResponseEntity<ApiResponse<MarcaDTO>> crear(@Valid @RequestBody MarcaDTO dto) {
        MarcaDTO creada = marcaService.crear(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creada.getId()).toUri();
        return ResponseEntity.created(location).body(ApiResponse.ok("Marca creada", creada));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar marca completa")
    public ResponseEntity<ApiResponse<MarcaDTO>> actualizar(
            @PathVariable Long id, @Valid @RequestBody MarcaDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok("Marca actualizada", marcaService.actualizar(id, dto)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar marca")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        marcaService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.ok("Marca eliminada", null));
    }

    @PatchMapping("/{id}/desactivar")
    @Operation(summary = "Desactivar marca (borrado lógico)")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        marcaService.desactivar(id);
        return ResponseEntity.ok(ApiResponse.ok("Marca desactivada", null));
    }
}
