package com.ventastech.pedido.model;

import lombok.*;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class HistorialEstado {
    private String estadoAnterior;
    private String estadoNuevo;
    private String motivo;
    private String usuarioAccion;
    private LocalDateTime fecha;
}