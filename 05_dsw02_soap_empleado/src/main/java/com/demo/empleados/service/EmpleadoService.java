package com.demo.empleados.service;

import com.demo.empleados.model.Empleado;
import jakarta.jws.WebMethod;
import jakarta.jws.WebService;

import java.util.List;

@WebService
public interface EmpleadoService {

    @WebMethod
    List<Empleado> listarEmpleado();

}
