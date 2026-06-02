package com.finanzasperu.ws.endpoint;

import com.finanzasperu.ws.dto.ClienteDTO;
import com.finanzasperu.ws.dto.RespuestaDTO;
import com.finanzasperu.ws.service.ClienteService;
import jakarta.jws.WebService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Implementacion del endpoint SOAP de Gestion de Clientes.
 *
 * Responsabilidad de esta clase:
 *  - Recibir la peticion SOAP (ya deserializada por CXF).
 *  - Delegar 100% la logica al ClienteService.
 *  - Devolver la respuesta (CXF la serializa a XML).
 *
 * IMPORTANTE:
 *  endpointInterface = "..." vincula esta clase con el contrato (interface).
 *  El WSDL se genera desde IGestionClienteWS, no desde esta clase.
 *  serviceName y portName aparecen en el WSDL generado.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@WebService(
        endpointInterface = "com.finanzasperu.ws.endpoint.IGestionClienteWS",
        serviceName        = "GestionClienteService",
        portName           = "GestionClientePort",
        targetNamespace    = "http://ws.finanzasperu.com/clientes"
)
public class GestionClienteWSImpl implements IGestionClienteWS {

    private final ClienteService clienteService;

    @Override
    public RespuestaDTO registrarCliente(ClienteDTO clienteDTO) {
        log.info("[SOAP] registrarCliente - doc={}", clienteDTO.getNroDocumento());
        return clienteService.crear(clienteDTO);
    }

    @Override
    public RespuestaDTO consultarClientePorId(Long idCliente) {
        log.info("[SOAP] consultarClientePorId - id={}", idCliente);
        return clienteService.buscarPorId(idCliente);
    }

    @Override
    public RespuestaDTO consultarClientePorDocumento(String nroDocumento) {
        log.info("[SOAP] consultarClientePorDocumento - doc={}", nroDocumento);
        return clienteService.buscarPorDocumento(nroDocumento);
    }

    @Override
    public RespuestaDTO listarTodosClientes() {
        log.info("[SOAP] listarTodosClientes");
        return clienteService.listarTodos();
    }

    @Override
    public RespuestaDTO listarClientesActivos() {
        log.info("[SOAP] listarClientesActivos");
        return clienteService.listarActivos();
    }

    @Override
    public RespuestaDTO buscarClientes(String termino) {
        log.info("[SOAP] buscarClientes - termino={}", termino);
        return clienteService.buscar(termino);
    }

    @Override
    public RespuestaDTO actualizarCliente(Long idCliente, ClienteDTO clienteDTO) {
        log.info("[SOAP] actualizarCliente - id={}", idCliente);
        return clienteService.actualizar(idCliente, clienteDTO);
    }

    @Override
    public RespuestaDTO cambiarEstadoCliente(Long idCliente, String estado) {
        log.info("[SOAP] cambiarEstadoCliente - id={}, estado={}", idCliente, estado);
        return clienteService.cambiarEstado(idCliente, estado);
    }

    @Override
    public RespuestaDTO eliminarCliente(Long idCliente) {
        log.info("[SOAP] eliminarCliente - id={}", idCliente);
        return clienteService.eliminar(idCliente);
    }
}
