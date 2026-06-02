package com.finanzasperu.ws.dto;

import jakarta.xml.bind.annotation.*;
import lombok.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RespuestaDTO", propOrder = {"codigo", "mensaje", "cliente", "clientes"})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RespuestaDTO {

    private String          codigo;
    private String          mensaje;
    private ClienteDTO      cliente;    // para operaciones de un solo cliente
    private ListaClientesDTO clientes;  // para listar/buscar

    public static RespuestaDTO exitoUno(String mensaje, ClienteDTO dto) {
        return RespuestaDTO.builder().codigo("00").mensaje(mensaje).cliente(dto).build();
    }

    public static RespuestaDTO exitoLista(String mensaje, ListaClientesDTO lista) {
        return RespuestaDTO.builder().codigo("00").mensaje(mensaje).clientes(lista).build();
    }

    public static RespuestaDTO error(String mensaje) {
        return RespuestaDTO.builder().codigo("01").mensaje(mensaje).build();
    }
}