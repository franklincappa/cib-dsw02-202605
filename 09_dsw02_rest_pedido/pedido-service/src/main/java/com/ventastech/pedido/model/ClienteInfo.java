package com.ventastech.pedido.model;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ClienteInfo {
    private String clienteId;
    private String nombre;
    private String email;
    private String telefono;
    private String direccionEntrega;
    private String ciudad;
    private String referencia;
}