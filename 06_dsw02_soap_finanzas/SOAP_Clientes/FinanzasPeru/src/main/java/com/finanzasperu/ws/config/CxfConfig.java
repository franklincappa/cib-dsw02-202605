package com.finanzasperu.ws.config;

import com.finanzasperu.ws.endpoint.GestionClienteWSImpl;
import jakarta.xml.ws.Endpoint;
import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuracion de Apache CXF.
 *
 * Responsabilidad:
 *  - Registrar cada endpoint SOAP y publicarlo en una URL especifica.
 *  - El Bus de CXF es el nucleo que gestiona el ciclo de vida de los endpoints.
 *  - Spring inyecta el Bus automaticamente (viene del cxf-spring-boot-starter-jaxws).
 *
 * URL final de cada servicio:
 *  Clientes:   http://localhost:8099/ws/clientes
 *  WSDL:       http://localhost:8099/ws/clientes?wsdl
 *  Servicios:  http://localhost:8099/ws
 *
 * Para agregar nuevos servicios (cuentas, prestamos, etc.):
 *  1. Crear la interface @WebService
 *  2. Crear la implementacion @WebService(endpointInterface=...)
 *  3. Agregar un @Bean Endpoint aqui con su ruta /ws/nombre
 */
@Configuration
public class CxfConfig {

    @Bean
    public Endpoint endpointClientes(Bus bus, GestionClienteWSImpl impl) {
        EndpointImpl endpoint = new EndpointImpl(bus, impl);
        endpoint.publish("/clientes");
        return endpoint;
    }

    /*
     * Ejemplo de como se agregarian los demas servicios del sistema:
     *
     * @Bean
     * public Endpoint endpointCuentas(Bus bus, GestionCuentaWSImpl impl) {
     *     EndpointImpl endpoint = new EndpointImpl(bus, impl);
     *     endpoint.publish("/cuentas");
     *     return endpoint;
     * }
     *
     * @Bean
     * public Endpoint endpointPrestamos(Bus bus, GestionPrestamoWSImpl impl) {
     *     EndpointImpl endpoint = new EndpointImpl(bus, impl);
     *     endpoint.publish("/prestamos");
     *     return endpoint;
     * }
     */
}
