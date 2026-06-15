package com.ventastech.pedido.service;

import com.ventastech.pedido.dto.*;
import com.ventastech.pedido.model.enums.EstadoPedido;
import java.util.List;

public interface PedidoService {
    PedidoDTO crear(CrearPedidoDTO dto);
    PedidoDTO obtenerPorId(String id);
    PedidoDTO obtenerPorNumero(String numeroPedido);
    List<PedidoDTO> listarTodos();
    List<PedidoDTO> listarPorEstado(EstadoPedido estado);
    List<PedidoDTO> listarPorCliente(String clienteId);
    List<PedidoDTO> listarRecientes();
    PedidoDTO cambiarEstado(String id, CambioEstadoDTO dto);
    PedidoDTO cancelar(String id, String motivo);
    void eliminar(String id);
}
