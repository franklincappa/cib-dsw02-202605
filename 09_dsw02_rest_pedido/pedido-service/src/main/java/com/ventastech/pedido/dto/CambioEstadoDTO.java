package com.ventastech.pedido.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CambioEstadoDTO {

    @NotBlank(message = "El nuevo estado es obligatorio")
    private String nuevoEstado;

    private String motivo;
    private String usuarioAccion;
}
