package com.ventastech.pedido.service.impl;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.ventastech.pedido.exception.ResourceNotFoundException;
import com.ventastech.pedido.model.ItemPedido;
import com.ventastech.pedido.model.Pedido;
import com.ventastech.pedido.model.enums.EstadoPedido;
import com.ventastech.pedido.repository.PedidoRepository;
import com.ventastech.pedido.service.ReporteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReporteServiceImpl implements ReporteService {

    private final PedidoRepository pedidoRepository;

    private static final DateTimeFormatter FMT_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FMT_DIA   = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DeviceRgb AZUL_HEADER = new DeviceRgb(30, 58, 95);
    private static final DeviceRgb AZUL_CLARO  = new DeviceRgb(46, 117, 182);
    private static final DeviceRgb GRIS_FILA   = new DeviceRgb(242, 242, 242);
    private static final DeviceRgb VERDE       = new DeviceRgb(26, 107, 60);

    // ── PDF de un pedido ─────────────────────────────────
    @Override
    public byte[] generarPdfPedido(String pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado: " + pedidoId));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PdfWriter writer = new PdfWriter(baos);
             PdfDocument pdf = new PdfDocument(writer);
             Document doc = new Document(pdf)) {

            doc.setMargins(40, 50, 40, 50);

            // ── Encabezado ──────────────────────────────
            agregarEncabezadoPedido(doc, pedido);

            // ── Datos del cliente ────────────────────────
            doc.add(new Paragraph("\n"));
            agregarSeccion(doc, "Datos del cliente");
            if (pedido.getClienteInfo() != null) {
                Table tCliente = new Table(UnitValue.createPercentArray(new float[]{1, 2}))
                        .setWidth(UnitValue.createPercentValue(100));
                agregarFilaTabla(tCliente, "Nombre", pedido.getClienteInfo().getNombre(), false);
                agregarFilaTabla(tCliente, "Email",  pedido.getClienteInfo().getEmail(),  true);
                agregarFilaTabla(tCliente, "Teléfono", pedido.getClienteInfo().getTelefono() != null ?
                        pedido.getClienteInfo().getTelefono() : "-", false);
                agregarFilaTabla(tCliente, "Dirección", pedido.getClienteInfo().getDireccionEntrega(), true);
                agregarFilaTabla(tCliente, "Ciudad", pedido.getClienteInfo().getCiudad(), false);
                doc.add(tCliente);
            }

            // ── Items del pedido ────────────────────────
            doc.add(new Paragraph("\n"));
            agregarSeccion(doc, "Detalle del pedido");
            Table tItems = new Table(UnitValue.createPercentArray(new float[]{3, 1, 1.5f, 1.5f}))
                    .setWidth(UnitValue.createPercentValue(100));

            // Headers
            agregarHeaderTabla(tItems, "Producto");
            agregarHeaderTabla(tItems, "Cant.");
            agregarHeaderTabla(tItems, "Precio Unit.");
            agregarHeaderTabla(tItems, "Subtotal");

            boolean gris = false;
            for (ItemPedido item : pedido.getItems()) {
                agregarCeldaItem(tItems, item.getNombreProducto() + "\n" + item.getCodigoSku(), gris, false);
                agregarCeldaItem(tItems, String.valueOf(item.getCantidad()), gris, true);
                agregarCeldaItem(tItems, "S/ " + item.getPrecioUnitario(), gris, true);
                agregarCeldaItem(tItems, "S/ " + item.getSubtotal(), gris, true);
                gris = !gris;
            }
            doc.add(tItems);

            // ── Totales ──────────────────────────────────
            doc.add(new Paragraph("\n"));
            Table tTotales = new Table(UnitValue.createPercentArray(new float[]{3, 1}))
                    .setWidth(UnitValue.createPercentValue(50))
                    .setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.RIGHT);

            agregarFilaTotales(tTotales, "Subtotal:",   "S/ " + pedido.getSubtotal(), false);
            agregarFilaTotales(tTotales, "IGV (18%):",  "S/ " + pedido.getImpuesto(), false);
            agregarFilaTotalesDestacado(tTotales, "TOTAL:", "S/ " + pedido.getTotal());
            doc.add(tTotales);

            // ── Notas ────────────────────────────────────
            if (pedido.getNotas() != null && !pedido.getNotas().isBlank()) {
                doc.add(new Paragraph("\n"));
                agregarSeccion(doc, "Notas");
                doc.add(new Paragraph(pedido.getNotas()).setFontSize(10));
            }

            // ── Pie de página ───────────────────────────
            doc.add(new Paragraph("\n\n"));
            doc.add(new Paragraph("Generado el: " + LocalDateTime.now().format(FMT_FECHA))
                    .setFontSize(8).setFontColor(ColorConstants.GRAY)
                    .setTextAlignment(TextAlignment.CENTER));
            doc.add(new Paragraph("VentasTech — pedidos@ventastech.com")
                    .setFontSize(8).setFontColor(ColorConstants.GRAY)
                    .setTextAlignment(TextAlignment.CENTER));

        } catch (Exception e) {
            log.error("Error generando PDF del pedido {}: {}", pedidoId, e.getMessage());
            throw new RuntimeException("Error generando PDF: " + e.getMessage());
        }

        log.info("PDF generado para pedido: {}", pedido.getNumeroPedido());
        return baos.toByteArray();
    }

    // ── Reporte de pedidos del día ────────────────────────
    @Override
    public byte[] generarReportePedidosDia() {
        LocalDateTime inicioDia = LocalDate.now().atStartOfDay();
        LocalDateTime finDia    = inicioDia.plusDays(1).minusSeconds(1);
        List<Pedido> pedidos    = pedidoRepository.findByFechaCreacionBetween(inicioDia, finDia);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PdfWriter writer = new PdfWriter(baos);
             PdfDocument pdf = new PdfDocument(writer);
             Document doc = new Document(pdf)) {

            doc.setMargins(40, 50, 40, 50);

            // ── Encabezado reporte ───────────────────────
            doc.add(new Paragraph("VentasTech")
                    .setFontSize(22).setBold()
                    .setFontColor(new DeviceRgb(30, 58, 95))
                    .setTextAlignment(TextAlignment.CENTER));
            doc.add(new Paragraph("Reporte de Pedidos del Día")
                    .setFontSize(16).setBold()
                    .setFontColor(AZUL_CLARO)
                    .setTextAlignment(TextAlignment.CENTER));
            doc.add(new Paragraph("Fecha: " + LocalDate.now().format(FMT_DIA))
                    .setFontSize(11).setTextAlignment(TextAlignment.CENTER));
            doc.add(new Paragraph("Total de pedidos: " + pedidos.size())
                    .setFontSize(11).setTextAlignment(TextAlignment.CENTER));
            doc.add(new Paragraph("\n"));

            // ── Resumen por estado ───────────────────────
            agregarSeccion(doc, "Resumen por estado");
            Table tResumen = new Table(UnitValue.createPercentArray(new float[]{2, 1, 1.5f}))
                    .setWidth(UnitValue.createPercentValue(60));
            agregarHeaderTabla(tResumen, "Estado");
            agregarHeaderTabla(tResumen, "Cantidad");
            agregarHeaderTabla(tResumen, "Total S/");

            boolean g = false;
            for (EstadoPedido estado : EstadoPedido.values()) {
                List<Pedido> porEstado = pedidos.stream()
                        .filter(p -> p.getEstado() == estado).toList();
                if (porEstado.isEmpty()) continue;
                BigDecimal totalEstado = porEstado.stream()
                        .map(Pedido::getTotal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                agregarCeldaItem(tResumen, estado.name(), g, false);
                agregarCeldaItem(tResumen, String.valueOf(porEstado.size()), g, true);
                agregarCeldaItem(tResumen, "S/ " + totalEstado, g, true);
                g = !g;
            }
            doc.add(tResumen);

            // ── Listado detallado ────────────────────────
            doc.add(new Paragraph("\n"));
            agregarSeccion(doc, "Listado de pedidos");
            Table tPedidos = new Table(UnitValue.createPercentArray(
                    new float[]{1.5f, 2, 2, 1.2f, 1, 1.2f}))
                    .setWidth(UnitValue.createPercentValue(100));

            agregarHeaderTabla(tPedidos, "N° Pedido");
            agregarHeaderTabla(tPedidos, "Cliente");
            agregarHeaderTabla(tPedidos, "Hora");
            agregarHeaderTabla(tPedidos, "Estado");
            agregarHeaderTabla(tPedidos, "Items");
            agregarHeaderTabla(tPedidos, "Total S/");

            boolean gris2 = false;
            for (Pedido p : pedidos) {
                String cliente = p.getClienteInfo() != null ? p.getClienteInfo().getNombre() : p.getClienteId();
                int totalItemsCant = p.getItems().stream().mapToInt(ItemPedido::getCantidad).sum();
                agregarCeldaItem(tPedidos, p.getNumeroPedido(), gris2, false);
                agregarCeldaItem(tPedidos, cliente, gris2, false);
                agregarCeldaItem(tPedidos, p.getFechaCreacion().format(FMT_FECHA), gris2, false);
                agregarCeldaItem(tPedidos, p.getEstado().name(), gris2, true);
                agregarCeldaItem(tPedidos, String.valueOf(totalItemsCant), gris2, true);
                agregarCeldaItem(tPedidos, "S/ " + p.getTotal(), gris2, true);
                gris2 = !gris2;
            }
            doc.add(tPedidos);

            // ── Total general ────────────────────────────
            doc.add(new Paragraph("\n"));
            BigDecimal totalDia = pedidos.stream()
                    .map(Pedido::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            Table tTotal = new Table(UnitValue.createPercentArray(new float[]{3, 1}))
                    .setWidth(UnitValue.createPercentValue(40))
                    .setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.RIGHT);
            agregarFilaTotalesDestacado(tTotal, "TOTAL DEL DÍA:", "S/ " + totalDia);
            doc.add(tTotal);

            // ── Pie ──────────────────────────────────────
            doc.add(new Paragraph("\n\n"));
            doc.add(new Paragraph("Generado el: " + LocalDateTime.now().format(FMT_FECHA))
                    .setFontSize(8).setFontColor(ColorConstants.GRAY)
                    .setTextAlignment(TextAlignment.CENTER));

        } catch (Exception e) {
            log.error("Error generando reporte del día: {}", e.getMessage());
            throw new RuntimeException("Error generando reporte: " + e.getMessage());
        }

        return baos.toByteArray();
    }

    // ── Helpers PDF ───────────────────────────────────────

    private void agregarEncabezadoPedido(Document doc, Pedido p) {
        doc.add(new Paragraph("VentasTech")
                .setFontSize(22).setBold()
                .setFontColor(AZUL_HEADER)
                .setTextAlignment(TextAlignment.CENTER));
        doc.add(new Paragraph("Pedido: " + p.getNumeroPedido())
                .setFontSize(16).setBold()
                .setFontColor(AZUL_CLARO)
                .setTextAlignment(TextAlignment.CENTER));

        Table tHeader = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                .setWidth(UnitValue.createPercentValue(100));
        agregarFilaTabla(tHeader, "Fecha",  p.getFechaCreacion().format(FMT_FECHA), false);
        agregarFilaTabla(tHeader, "Estado", p.getEstado().name(), false);
        if (p.getFechaEntrega() != null)
            agregarFilaTabla(tHeader, "Entregado", p.getFechaEntrega().format(FMT_FECHA), true);
        doc.add(tHeader);
    }

    private void agregarSeccion(Document doc, String titulo) {
        doc.add(new Paragraph(titulo)
                .setFontSize(12).setBold()
                .setFontColor(AZUL_HEADER)
                .setBorderBottom(new com.itextpdf.layout.borders.SolidBorder(AZUL_CLARO, 1)));
        doc.add(new Paragraph("\n").setFontSize(4));
    }

    private void agregarHeaderTabla(Table table, String texto) {
        table.addHeaderCell(new Cell()
                .add(new Paragraph(texto).setBold().setFontSize(10)
                        .setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(AZUL_HEADER)
                .setPadding(6));
    }

    private void agregarFilaTabla(Table table, String label, String valor, boolean gris) {
        table.addCell(new Cell()
                .add(new Paragraph(label).setBold().setFontSize(10))
                .setBackgroundColor(gris ? GRIS_FILA : ColorConstants.WHITE)
                .setPadding(5));
        table.addCell(new Cell()
                .add(new Paragraph(valor != null ? valor : "-").setFontSize(10))
                .setBackgroundColor(gris ? GRIS_FILA : ColorConstants.WHITE)
                .setPadding(5));
    }

    private void agregarCeldaItem(Table table, String texto, boolean gris, boolean centrado) {
        Paragraph p = new Paragraph(texto != null ? texto : "-").setFontSize(9);
        if (centrado) p.setTextAlignment(TextAlignment.CENTER);
        table.addCell(new Cell()
                .add(p)
                .setBackgroundColor(gris ? GRIS_FILA : ColorConstants.WHITE)
                .setPadding(5));
    }

    private void agregarFilaTotales(Table table, String label, String valor, boolean destacado) {
        table.addCell(new Cell()
                .add(new Paragraph(label).setBold().setFontSize(10))
                .setPadding(5).setBorder(com.itextpdf.layout.borders.Border.NO_BORDER));
        table.addCell(new Cell()
                .add(new Paragraph(valor).setFontSize(10).setTextAlignment(TextAlignment.RIGHT))
                .setPadding(5).setBorder(com.itextpdf.layout.borders.Border.NO_BORDER));
    }

    private void agregarFilaTotalesDestacado(Table table, String label, String valor) {
        table.addCell(new Cell()
                .add(new Paragraph(label).setBold().setFontSize(12)
                        .setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(AZUL_HEADER).setPadding(6));
        table.addCell(new Cell()
                .add(new Paragraph(valor).setBold().setFontSize(12)
                        .setFontColor(ColorConstants.WHITE)
                        .setTextAlignment(TextAlignment.RIGHT))
                .setBackgroundColor(AZUL_HEADER).setPadding(6));
    }
}
