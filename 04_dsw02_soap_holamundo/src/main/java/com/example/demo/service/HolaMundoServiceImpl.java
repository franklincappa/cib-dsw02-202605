package com.example.demo.service;

import jakarta.jws.WebService;

@WebService (endpointInterface = "com.example.demo.service.HolaMundoService")
public class HolaMundoServiceImpl implements HolaMundoService {

    @Override
    public String holaMundo() {
        return "Hola Mundo desde un servicio SOAP";
    }

    @Override
    public String holaMundov2() {
        return "Web Service SOAP v2";
    }

    @Override
    public int sumar(int a, int b) {
        return a+b;
    }
}
