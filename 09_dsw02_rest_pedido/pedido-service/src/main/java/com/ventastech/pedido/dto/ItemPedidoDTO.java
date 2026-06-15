package com.ventastech.pedido.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ItemPedidoDTO {

    @NotNull(message = "El productoId es obligatorio")
    private Long productoId;

    @NotBlank(message = "El SKU es obligatorio")
    private String codigoSku;

    @NotBlank(message = "El nombre del producto es obligatorio")
    private String nombreProducto;

    private String descripcionProducto;

    @NotNull(message = "El precio unitario es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    private BigDecimal precioUnitario;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad mínima es 1")
    private Integer cantidad;

    private BigDecimal subtotal; // calculado automáticamente
}
