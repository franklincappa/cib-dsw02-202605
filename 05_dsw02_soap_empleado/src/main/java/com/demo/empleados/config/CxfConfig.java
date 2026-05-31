package com.demo.empleados.config;

import com.demo.empleados.service.EmpleadoService;
import com.demo.empleados.service.EmpleadoServiceImpl;
import jakarta.xml.ws.Endpoint;
import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CxfConfig {
    @Bean
    public EmpleadoService empleadoService(){
        return new EmpleadoServiceImpl();
    }

    @Bean
    public Endpoint empleadoEndpoint(Bus bus, EmpleadoService service){
        EndpointImpl endpoint = new EndpointImpl(bus, service);
        endpoint.publish("/empleado");
        return endpoint;
    }

}



