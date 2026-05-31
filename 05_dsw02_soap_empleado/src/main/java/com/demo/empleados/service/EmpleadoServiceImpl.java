package com.demo.empleados.service;

import com.demo.empleados.model.Empleado;

import java.util.List;

public class EmpleadoServiceImpl implements EmpleadoService {
    @Override
    public List<Empleado> listarEmpleado() {
        return List.of(
                new Empleado(1, "45037020", "Miguel", "Ramos","Cercado Lima", "mmiguel@gmail.com", "123456789", "Marketing"),
                new Empleado(2, "45037021", "Luis", "Elk","Cercado Lima", "mmiguel@gmail.com", "123456789", "Marketing"),
                new Empleado(3 ,"45037022", "Marlene", "Mamani","Cercado Lima", "mmiguel@gmail.com", "123456789", "Marketing"),
                new Empleado(4, "45037023", "Daniela", "Ramos","Cercado Lima", "mmiguel@gmail.com", "123456789", "Marketing")

        );
    }
}
