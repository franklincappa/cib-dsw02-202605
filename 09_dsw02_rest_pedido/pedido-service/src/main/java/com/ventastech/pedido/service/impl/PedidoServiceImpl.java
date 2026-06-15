package com.ventastech.pedido.service.impl;

import com.ventastech.pedido.dto.*;
import com.ventastech.pedido.exception.*;
import com.ventastech.pedido.model.*;
import com.ventastech.pedido.model.enums.EstadoPedido;
import com.ventastech.pedido.repository.PedidoRepository;
import com.ventastech.pedido.service.PedidoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private static final BigDecimal IGV = new BigDecimal("0.18");
    private static final AtomicLong CONTADOR = new AtomicLong(
            System.currentTimeMillis() % 100000
    );

    // ── Generador de número de pedido ────────────────────
    private String generarNumeroPedido() {
        String anio = String.valueOf(LocalDateTime.now().getYear());
        String secuencia = String.format("%06d", CONTADOR.incrementAndGet());
        return "VT-" + anio + "-" + secuencia;
    }

    // ── Mapeo ─────────────────────────────────────────────
    private ClienteInfo toClienteInfo(ClienteInfoDTO dto) {
        return ClienteInfo.builder()
                .clienteId(dto.getClienteId())
                .nombre(dto.getNombre())
                .email(dto.getEmail())
                .telefono(dto.getTelefono())
                .direccionEntrega(dto.getDireccionEntrega())
                .ciudad(dto.getCiudad())
                .referencia(dto.getReferencia())
                .build();
    }

    private ClienteInfoDTO toClienteInfoDTO(ClienteInfo c) {
        return ClienteInfoDTO.builder()
                .clienteId(c.getClienteId())
                .nombre(c.getNombre())
                .email(c.getEmail())
                .telefono(c.getTelefono())
                .direccionEntrega(c.getDireccionEntrega())
                .ciudad(c.getCiudad())
                .referencia(c.getReferencia())
                .build();
    }

    private ItemPedido toItemPedido(ItemPedidoDTO dto) {
        BigDecimal subtotal = dto.getPrecioUnitario()
                .multiply(BigDecimal.valueOf(dto.getCantidad()))
                .setScale(2, RoundingMode.HALF_UP);
        return ItemPedido.builder()
                .productoId(dto.getProductoId())
                .codigoSku(dto.getCodigoSku())
                .nombreProducto(dto.getNombreProducto())
                .descripcionProducto(dto.getDescripcionProducto())
                .precioUnitario(dto.getPrecioUnitario())
                .cantidad(dto.getCantidad())
                .subtotal(subtotal)
                .build();
    }

    private ItemPedidoDTO toItemDTO(ItemPedido item) {
        return ItemPedidoDTO.builder()
                .productoId(item.getProductoId())
                .codigoSku(item.getCodigoSku())
                .nombreProducto(item.getNombreProducto())
                .descripcionProducto(item.getDescripcionProducto())
                .precioUnitario(item.getPrecioUnitario())
                .cantidad(item.getCantidad())
                .subtotal(item.getSubtotal())
                .build();
    }

    private PedidoDTO toDTO(Pedido p) {
        return PedidoDTO.builder()
                .id(p.getId())
                .numeroPedido(p.getNumeroPedido())
                .clienteId(p.getClienteId())
                .clienteInfo(p.getClienteInfo() != null ? toClienteInfoDTO(p.getClienteInfo()) : null)
                .items(p.getItems().stream().map(this::toItemDTO).collect(Collectors.toList()))
                .estado(p.getEstado().name())
                .subtotal(p.getSubtotal())
                .impuesto(p.getImpuesto())
                .total(p.getTotal())
                .notas(p.getNotas())
                .motivoCancelacion(p.getMotivoCancelacion())
                .fechaCreacion(p.getFechaCreacion())
                .fechaActualizacion(p.getFechaActualizacion())
                .fechaEntrega(p.getFechaEntrega())
                .totalItems(p.getItems().stream().mapToInt(ItemPedido::getCantidad).sum())
                .build();
    }

    // ── Cálculo de totales ────────────────────────────────
    private void calcularTotales(Pedido pedido) {
        BigDecimal subtotal = pedido.getItems().stream()
                .map(ItemPedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal impuesto = subtotal.multiply(IGV)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(impuesto)
                .setScale(2, RoundingMode.HALF_UP);
        pedido.setSubtotal(subtotal);
        pedido.setImpuesto(impuesto);
        pedido.setTotal(total);
    }

    // ── CRUD ──────────────────────────────────────────────

    @Override
    public PedidoDTO crear(CrearPedidoDTO dto) {
        List<ItemPedido> items = dto.getItems().stream()
                .map(this::toItemPedido).collect(Collectors.toList());

        Pedido pedido = Pedido.builder()
                .numeroPedido(generarNumeroPedido())
                .clienteId(dto.getClienteInfo().getClienteId())
                .clienteInfo(toClienteInfo(dto.getClienteInfo()))
                .items(items)
                .estado(EstadoPedido.PENDIENTE)
                .notas(dto.getNotas())
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();

        calcularTotales(pedido);

        // Primer registro en historial
        pedido.getHistorialEstados().add(HistorialEstado.builder()
                .estadoAnterior(null)
                .estadoNuevo(EstadoPedido.PENDIENTE.name())
                .motivo("Pedido creado")
                .fecha(LocalDateTime.now())
                .build());

        Pedido guardado = pedidoRepository.save(pedido);
        log.info("Pedido creado: {} — cliente: {}", guardado.getNumeroPedido(), guardado.getClienteId());
        return toDTO(guardado);
    }

    @Override
    public PedidoDTO obtenerPorId(String id) {
        return toDTO(pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con id: " + id)));
    }

    @Override
    public PedidoDTO obtenerPorNumero(String numeroPedido) {
        return toDTO(pedidoRepository.findByNumeroPedido(numeroPedido)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado: " + numeroPedido)));
    }

    @Override
    public List<PedidoDTO> listarTodos() {
        return pedidoRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<PedidoDTO> listarPorEstado(EstadoPedido estado) {
        return pedidoRepository.findByEstado(estado).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<PedidoDTO> listarPorCliente(String clienteId) {
        return pedidoRepository.findByClienteId(clienteId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<PedidoDTO> listarRecientes() {
        return pedidoRepository.findTop10ByOrderByFechaCreacionDesc()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public PedidoDTO cambiarEstado(String id, CambioEstadoDTO dto) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con id: " + id));

        EstadoPedido nuevoEstado;
        try {
            nuevoEstado = EstadoPedido.valueOf(dto.getNuevoEstado().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Estado inválido: " + dto.getNuevoEstado());
        }

        if (!pedido.getEstado().puedeTransicionarA(nuevoEstado)) {
            throw new BusinessException(
                    "No se puede cambiar de " + pedido.getEstado() + " a " + nuevoEstado);
        }

        // Registrar en historial
        pedido.getHistorialEstados().add(HistorialEstado.builder()
                .estadoAnterior(pedido.getEstado().name())
                .estadoNuevo(nuevoEstado.name())
                .motivo(dto.getMotivo())
                .usuarioAccion(dto.getUsuarioAccion())
                .fecha(LocalDateTime.now())
                .build());

        pedido.setEstado(nuevoEstado);
        pedido.setFechaActualizacion(LocalDateTime.now());

        if (nuevoEstado == EstadoPedido.ENTREGADO) {
            pedido.setFechaEntrega(LocalDateTime.now());
        }

        log.info("Pedido {} cambió estado a {}", pedido.getNumeroPedido(), nuevoEstado);
        return toDTO(pedidoRepository.save(pedido));
    }

    @Override
    public PedidoDTO cancelar(String id, String motivo) {
        CambioEstadoDTO dto = CambioEstadoDTO.builder()
                .nuevoEstado("CANCELADO")
                .motivo(motivo)
                .build();
        PedidoDTO resultado = cambiarEstado(id, dto);

        // Guardar motivo de cancelación
        Pedido pedido = pedidoRepository.findById(id).get();
        pedido.setMotivoCancelacion(motivo);
        pedidoRepository.save(pedido);

        return resultado;
    }

    @Override
    public void eliminar(String id) {
        if (!pedidoRepository.existsById(id))
            throw new ResourceNotFoundException("Pedido no encontrado con id: " + id);
        pedidoRepository.deleteById(id);
        log.info("Pedido eliminado id: {}", id);
    }
}