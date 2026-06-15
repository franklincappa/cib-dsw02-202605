package com.ventastech.pedido.repository;

import com.ventastech.pedido.model.Pedido;
import com.ventastech.pedido.model.enums.EstadoPedido;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends MongoRepository<Pedido, String> {

    Optional<Pedido> findByNumeroPedido(String numeroPedido);
    boolean existsByNumeroPedido(String numeroPedido);

    List<Pedido> findByEstado(EstadoPedido estado);
    List<Pedido> findByClienteId(String clienteId);
    List<Pedido> findByClienteIdAndEstado(String clienteId, EstadoPedido estado);

    // Pedidos del día para el reporte
    List<Pedido> findByFechaCreacionBetween(LocalDateTime inicio, LocalDateTime fin);

    // Pedidos del día por estado
    List<Pedido> findByEstadoAndFechaCreacionBetween(
            EstadoPedido estado, LocalDateTime inicio, LocalDateTime fin);

    // Buscar por datos del cliente embebido
    @Query("{ 'clienteInfo.email': ?0 }")
    List<Pedido> findByClienteEmail(String email);

    @Query("{ 'clienteInfo.clienteId': ?0, 'estado': ?1 }")
    List<Pedido> findByClienteIdYEstado(String clienteId, String estado);

    // Contar pedidos por estado para dashboard
    long countByEstado(EstadoPedido estado);

    // Pedidos recientes
    List<Pedido> findTop10ByOrderByFechaCreacionDesc();
}
