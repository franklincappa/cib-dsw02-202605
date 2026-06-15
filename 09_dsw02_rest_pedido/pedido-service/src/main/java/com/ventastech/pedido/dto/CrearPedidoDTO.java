package com.ventastech.pedido.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CrearPedidoDTO {

    @NotNull(message = "Los datos del cliente son obligatorios")
    @Valid
    private ClienteInfoDTO clienteInfo;

    @NotEmpty(message = "El pedido debe tener al menos un item")
    @Valid
    private List<ItemPedidoDTO> items;

    private String notas;
}
