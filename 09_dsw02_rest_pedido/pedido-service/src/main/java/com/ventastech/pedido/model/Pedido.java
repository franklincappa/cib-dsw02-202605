package com.ventastech.pedido.model;

import com.ventastech.pedido.model.enums.EstadoPedido;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "pedidos")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Pedido {

    @Id
    private String id;

    @Indexed(unique = true)
    private String numeroPedido;        // VT-2026-000001

    // Referencia datos del cliente
    private String clienteId;
    private ClienteInfo clienteInfo;

    // Items del pedido (documentos embebidos)
    @Builder.Default
    private List<ItemPedido> items = new ArrayList<>();

    // Historial de cambios de estado
    @Builder.Default
    private List<HistorialEstado> historialEstados = new ArrayList<>();

    private EstadoPedido estado;

    private BigDecimal subtotal;        // suma de items sin impuesto
    private BigDecimal impuesto;        // 18% IGV
    private BigDecimal total;           // subtotal + impuesto

    private String notas;
    private String motivoCancelacion;

    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private LocalDateTime fechaEntrega;
}
