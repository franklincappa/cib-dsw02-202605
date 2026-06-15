package com.ventastech.pedido.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class PedidoDTO {
    private String id;
    private String numeroPedido;
    private String clienteId;
    private ClienteInfoDTO clienteInfo;
    private List<ItemPedidoDTO> items;
    private String estado;
    private BigDecimal subtotal;
    private BigDecimal impuesto;
    private BigDecimal total;
    private String notas;
    private String motivoCancelacion;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private LocalDateTime fechaEntrega;
    private int totalItems;
}
