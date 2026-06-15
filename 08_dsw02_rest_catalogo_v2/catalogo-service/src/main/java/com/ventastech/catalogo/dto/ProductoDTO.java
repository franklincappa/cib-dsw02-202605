package com.ventastech.catalogo.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductoDTO {

    private Long id;

    private Long categoriaId;
    private String categoriaNombre;   // solo lectura

    private Long marcaId;
    private String marcaNombre;       // solo lectura

    @NotBlank(message = "El SKU es obligatorio")
    @Size(max = 50)
    private String codigoSku;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100)
    private String nombre;

    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    private BigDecimal precio;

    private Boolean activo;

    // Datos de inventario embebidos en la respuesta
    private Integer stockActual;
    private Integer stockMinimo;
    private Integer stockMaximo;
}