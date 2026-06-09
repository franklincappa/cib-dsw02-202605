package com.ventastech.catalogo.controller;

import com.ventastech.catalogo.dto.*;
import com.ventastech.catalogo.service.MarcaService;
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
public class MarcaController {

    private final MarcaService marcaService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<MarcaDTO>>> listarTodas() {
        return ResponseEntity.ok(ApiResponse.ok(marcaService.listarTodas()));
    }

    @GetMapping("/activas")
    public ResponseEntity<ApiResponse<List<MarcaDTO>>> listarActivas() {
        return ResponseEntity.ok(ApiResponse.ok(marcaService.listarActivas()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MarcaDTO>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(marcaService.obtenerPorId(id)));
    }

    @GetMapping("/pais")
    public ResponseEntity<ApiResponse<List<MarcaDTO>>> listarPorPais(@RequestParam String pais) {
        return ResponseEntity.ok(ApiResponse.ok(marcaService.listarPorPais(pais)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MarcaDTO>> crear(@Valid @RequestBody MarcaDTO dto) {
        MarcaDTO creada = marcaService.crear(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creada.getId()).toUri();
        return ResponseEntity.created(location).body(ApiResponse.ok("Marca creada", creada));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MarcaDTO>> actualizar(
            @PathVariable Long id, @Valid @RequestBody MarcaDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok("Marca actualizada", marcaService.actualizar(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        marcaService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.ok("Marca eliminada", null));
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Long id) {
        marcaService.desactivar(id);
        return ResponseEntity.ok(ApiResponse.ok("Marca desactivada", null));
    }
}