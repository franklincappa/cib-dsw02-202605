package com.ventastech.pedido.dto;

import lombok.*;
import java.io.Serializable;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ApiResponse<T> implements Serializable {
    private boolean success;
    private String mensaje;
    private T data;

    public static <T> ApiResponse<T> ok(T data) {
        return ApiResponse.<T>builder().success(true).mensaje("OK").data(data).build();
    }
    public static <T> ApiResponse<T> ok(String mensaje, T data) {
        return ApiResponse.<T>builder().success(true).mensaje(mensaje).data(data).build();
    }
    public static <T> ApiResponse<T> error(String mensaje) {
        return ApiResponse.<T>builder().success(false).mensaje(mensaje).data(null).build();
    }
}
