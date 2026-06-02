package com.finanzasperu.ws.endpoint;

import com.finanzasperu.ws.dto.ClienteDTO;
import com.finanzasperu.ws.dto.RespuestaDTO;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

/**
 * Contrato SOAP del servicio de Gestion de Clientes.
 *
 * Esta interface define el WSDL que CXF genera automaticamente.
 * Cada @WebMethod es una operacion visible para el cliente SOAP.
 * Los @WebParam nombran los argumentos en el XML del mensaje.
 *
 * URL del WSDL: http://localhost:8099/ws/clientes?wsdl
 */
@WebService(name = "IGestionClienteWS",
        targetNamespace = "http://ws.finanzasperu.com/clientes")
public interface IGestionClienteWS {

    @WebMethod(operationName = "registrarCliente")
    RespuestaDTO registrarCliente(
            @WebParam(name = "clienteDTO") ClienteDTO clienteDTO
    );

    @WebMethod(operationName = "consultarClientePorId")
    RespuestaDTO consultarClientePorId(
            @WebParam(name = "idCliente") Long idCliente
    );

    @WebMethod(operationName = "consultarClientePorDocumento")
    RespuestaDTO consultarClientePorDocumento(
            @WebParam(name = "nroDocumento") String nroDocumento
    );

    @WebMethod(operationName = "listarTodosClientes")
    RespuestaDTO listarTodosClientes();

    @WebMethod(operationName = "listarClientesActivos")
    RespuestaDTO listarClientesActivos();

    @WebMethod(operationName = "buscarClientes")
    RespuestaDTO buscarClientes(
            @WebParam(name = "termino") String termino
    );

    @WebMethod(operationName = "actualizarCliente")
    RespuestaDTO actualizarCliente(
            @WebParam(name = "idCliente") Long idCliente,
            @WebParam(name = "clienteDTO") ClienteDTO clienteDTO
    );

    @WebMethod(operationName = "cambiarEstadoCliente")
    RespuestaDTO cambiarEstadoCliente(
            @WebParam(name = "idCliente") Long idCliente,
            @WebParam(name = "estado")     String estado
    );

    @WebMethod(operationName = "eliminarCliente")
    RespuestaDTO eliminarCliente(
            @WebParam(name = "idCliente") Long idCliente
    );
}
