package com.example.demo.service;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

@WebService
public interface HolaMundoService {

    @WebMethod
    String holaMundo();

    @WebMethod
    String holaMundov2();

    @WebMethod
    int sumar(@WebParam(name = "valor01") int a,
              @WebParam(name = "valor02") int b);
}

