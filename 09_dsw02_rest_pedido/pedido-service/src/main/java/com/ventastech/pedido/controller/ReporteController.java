package com.ventastech.pedido.controller;

import com.ventastech.pedido.service.ReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reportes")
@RequiredArgsConstructor
@Tag(name = "Reportes PDF", description = "Generación de PDFs de pedidos y reportes del día")
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping("/pedido/{pedidoId}/pdf")
    @Operation(summary = "Generar PDF de un pedido",
            description = "Retorna el PDF del pedido con datos del cliente, items y totales.")
    public ResponseEntity<byte[]> generarPdfPedido(
            @Parameter(description = "ID del pedido") @PathVariable String pedidoId) {

        byte[] pdf = reporteService.generarPdfPedido(pedidoId);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"pedido-" + pedidoId + ".pdf\"")
                .body(pdf);
    }

    @GetMapping("/dia/pdf")
    @Operation(summary = "Reporte PDF de pedidos del día",
            description = "Genera reporte con todos los pedidos del día actual, " +
                    "resumen por estado y total general.")
    public ResponseEntity<byte[]> generarReporteDia() {
        byte[] pdf = reporteService.generarReportePedidosDia();

        String nombreArchivo = "reporte-pedidos-" +
                java.time.LocalDate.now().format(
                        java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".pdf";

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + nombreArchivo + "\"")
                .body(pdf);
    }
}
