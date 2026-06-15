package com.ventastech.pedido.service;

public interface ReporteService {
    byte[] generarPdfPedido(String pedidoId);
    byte[] generarReportePedidosDia();
}