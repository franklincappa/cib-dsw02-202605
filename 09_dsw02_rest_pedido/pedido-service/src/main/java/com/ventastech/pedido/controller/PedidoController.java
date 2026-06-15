package com.ventastech.pedido.controller;

import com.ventastech.pedido.dto.*;
import com.ventastech.pedido.model.enums.EstadoPedido;
import com.ventastech.pedido.service.PedidoService;
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
@RequestMapping("/api/v1/pedidos")
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "Gestión de pedidos VentasTech")
public class PedidoController {

    private final PedidoService pedidoService;

    @GetMapping
    @Operation(summary = "Listar todos los pedidos")
    public ResponseEntity<ApiResponse<List<PedidoDTO>>> listarTodos() {
        return ResponseEntity.ok(ApiResponse.ok(pedidoService.listarTodos()));
    }

    @GetMapping("/recientes")
    @Operation(summary = "Últimos 10 pedidos creados")
    public ResponseEntity<ApiResponse<List<PedidoDTO>>> listarRecientes() {
        return ResponseEntity.ok(ApiResponse.ok(pedidoService.listarRecientes()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener pedido por ID")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Pedido encontrado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    public ResponseEntity<ApiResponse<PedidoDTO>> obtenerPorId(
            @Parameter(description = "ID del pedido") @PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(pedidoService.obtenerPorId(id)));
    }

    @GetMapping("/numero/{numeroPedido}")
    @Operation(summary = "Obtener pedido por número",
            description = "Ejemplo: VT-2026-000001")
    public ResponseEntity<ApiResponse<PedidoDTO>> obtenerPorNumero(
            @Parameter(description = "Número de pedido", example = "VT-2026-000001")
            @PathVariable String numeroPedido) {
        return ResponseEntity.ok(ApiResponse.ok(pedidoService.obtenerPorNumero(numeroPedido)));
    }

    @GetMapping("/estado/{estado}")
    @Operation(summary = "Listar pedidos por estado",
            description = "Estados: PENDIENTE, CONFIRMADO, EN_PROCESO, ENVIADO, ENTREGADO, CANCELADO")
    public ResponseEntity<ApiResponse<List<PedidoDTO>>> listarPorEstado(
            @Parameter(description = "Estado del pedido", example = "PENDIENTE")
            @PathVariable String estado) {
        EstadoPedido estadoEnum = EstadoPedido.valueOf(estado.toUpperCase());
        return ResponseEntity.ok(ApiResponse.ok(pedidoService.listarPorEstado(estadoEnum)));
    }

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Listar pedidos de un cliente")
    public ResponseEntity<ApiResponse<List<PedidoDTO>>> listarPorCliente(
            @Parameter(description = "ID del cliente", example = "CLI-001")
            @PathVariable String clienteId) {
        return ResponseEntity.ok(ApiResponse.ok(pedidoService.listarPorCliente(clienteId)));
    }

    @PostMapping
    @Operation(summary = "Crear nuevo pedido",
            description = "Crea el pedido en estado PENDIENTE con cálculo automático de IGV (18%).")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Pedido creado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<ApiResponse<PedidoDTO>> crear(@Valid @RequestBody CrearPedidoDTO dto) {
        PedidoDTO creado = pedidoService.crear(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creado.getId()).toUri();
        return ResponseEntity.created(location).body(ApiResponse.ok("Pedido creado", creado));
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Cambiar estado del pedido",
            description = "Transiciones válidas: PENDIENTE→CONFIRMADO, CONFIRMADO→EN_PROCESO, " +
                    "EN_PROCESO→ENVIADO, ENVIADO→ENTREGADO. CANCELADO es posible desde PENDIENTE, CONFIRMADO o EN_PROCESO.")
    public ResponseEntity<ApiResponse<PedidoDTO>> cambiarEstado(
            @PathVariable String id,
            @Valid @RequestBody CambioEstadoDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok("Estado actualizado", pedidoService.cambiarEstado(id, dto)));
    }

    @PatchMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar pedido")
    public ResponseEntity<ApiResponse<PedidoDTO>> cancelar(
            @PathVariable String id,
            @RequestParam(required = false) String motivo) {
        return ResponseEntity.ok(ApiResponse.ok("Pedido cancelado",
                pedidoService.cancelar(id, motivo)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar pedido (físico)",
            description = "Solo para uso administrativo.")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable String id) {
        pedidoService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.ok("Pedido eliminado", null));
    }
}
