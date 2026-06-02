package com.finanzasperu.ws.repository;

import com.finanzasperu.ws.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de Clientes.
 *
 * Patron Repository:
 *  - Abstrae el acceso a datos. La capa de servicio no conoce SQL.
 *  - JpaRepository provee findAll, findById, save, deleteById sin codigo extra.
 *  - Los metodos derivados (findByNroDocumento) generan SQL automaticamente.
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByNroDocumento(String nroDocumento);

    List<Cliente> findByEstado(String estado);

    List<Cliente> findByTipoCliente(String tipoCliente);

    boolean existsByNroDocumento(String nroDocumento);

    boolean existsByEmail(String email);

    @Query("SELECT c FROM Cliente c WHERE " +
            "LOWER(c.nombres) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
            "LOWER(c.apellidoPaterno) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
            "c.nroDocumento LIKE CONCAT('%', :termino, '%')")
    List<Cliente> buscarPorTermino(@Param("termino") String termino);
}
