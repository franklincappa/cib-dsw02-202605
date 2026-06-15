package com.ventastech.catalogo.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class InventarioDTO {

    private Long id;
    private Long productoId;
    private String productoNombre;    // solo lectura
    private String productoSku;       // solo lectura

    @Min(value = 0, message = "El stock actual no puede ser negativo")
    private Integer stockActual;

    @Min(value = 0, message = "El stock mínimo no puede ser negativo")
    private Integer stockMinimo;

    @Min(value = 0, message = "El stock máximo no puede ser negativo")
    private Integer stockMaximo;

    private Boolean bajoCritico;      // calculado: stockActual <= stockMinimo
}