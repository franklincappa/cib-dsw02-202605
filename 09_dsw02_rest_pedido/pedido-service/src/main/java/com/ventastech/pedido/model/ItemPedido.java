package com.ventastech.pedido.model;

import lombok.*;
import java.math.BigDecimal;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ItemPedido {

    // Referencia al microservicio catálogo
    private Long productoId;

    // Snapshot del producto al momento del pedido
    private String codigoSku;
    private String nombreProducto;
    private String descripcionProducto;
    private BigDecimal precioUnitario;
    private Integer cantidad;
    private BigDecimal subtotal; // precioUnitario * cantidad
}