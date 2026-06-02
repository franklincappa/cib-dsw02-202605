package com.finanzasperu.ws.service;

import com.finanzasperu.ws.dto.ClienteDTO;
import com.finanzasperu.ws.dto.RespuestaDTO;

/**
 * Contrato de negocio para la gestion de clientes.
 *
 * Patron Fachada (Facade):
 *  Esta interface es el punto unico de acceso a la logica de clientes.
 *  El endpoint SOAP solo conoce esta interface, no la implementacion.
 *  Facilita pruebas unitarias (se puede mockear facilmente).
 */
public interface ClienteService {

    RespuestaDTO crear(ClienteDTO dto);

    RespuestaDTO buscarPorId(Long idCliente);

    RespuestaDTO buscarPorDocumento(String nroDocumento);

    RespuestaDTO listarTodos();

    RespuestaDTO listarActivos();

    RespuestaDTO buscar(String termino);

    RespuestaDTO actualizar(Long idCliente, ClienteDTO dto);

    RespuestaDTO cambiarEstado(Long idCliente, String estado);

    RespuestaDTO eliminar(Long idCliente);
}
