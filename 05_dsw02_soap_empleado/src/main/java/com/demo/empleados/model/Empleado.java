package com.demo.empleados.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="Empleado")
public class Empleado {
    private long id;
    private String dni;
    private String nombres;
    private String apellidos;
    private String direccion;
    private String email;
    private String celular;
    private String area;

    public Empleado() {}

    public Empleado(long id, String dni, String nombres, String apellidos,
                    String direccion, String email, String celular, String area) {
        this.id = id;
        this.dni = dni;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.direccion = direccion;
        this.email = email;
        this.celular = celular;
        this.area = area;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCelular() { return celular; }
    public void setCelular(String celular) { this.celular = celular; }
    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }

}
