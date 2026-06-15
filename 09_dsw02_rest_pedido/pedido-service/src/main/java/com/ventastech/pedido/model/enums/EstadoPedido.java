package com.ventastech.pedido.model.enums;

public enum EstadoPedido {
    PENDIENTE,
    CONFIRMADO,
    EN_PROCESO,
    ENVIADO,
    ENTREGADO,
    CANCELADO;

    // Valida si la transición de estado es permitida
    public boolean puedeTransicionarA(EstadoPedido nuevo) {
        return switch (this) {
            case PENDIENTE   -> nuevo == CONFIRMADO || nuevo == CANCELADO;
            case CONFIRMADO  -> nuevo == EN_PROCESO || nuevo == CANCELADO;
            case EN_PROCESO  -> nuevo == ENVIADO    || nuevo == CANCELADO;
            case ENVIADO     -> nuevo == ENTREGADO;
            case ENTREGADO, CANCELADO -> false; // estados finales
        };
    }
}