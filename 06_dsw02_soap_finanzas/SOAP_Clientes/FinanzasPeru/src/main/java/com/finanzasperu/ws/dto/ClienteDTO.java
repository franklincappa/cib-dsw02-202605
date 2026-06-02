package com.finanzasperu.ws.dto;

import jakarta.xml.bind.annotation.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) de Cliente para el contrato SOAP.
 *
 * Patron DTO:
 *  - Desacopla el modelo de base de datos del contrato del servicio.
 *  - Permite exponer solo los campos necesarios al cliente SOAP.
 *  - Las anotaciones JAXB definen como se serializa a XML.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClienteDTO", propOrder = {
        "idCliente", "tipoDocumento", "nroDocumento",
        "nombres", "apellidoPaterno", "apellidoMaterno",
        "fechaNacimiento", "direccion", "telefono", "email",
        "tipoCliente", "estado", "fechaRegistro"
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteDTO {

    private Long   idCliente;
    private String tipoDocumento;
    private String nroDocumento;
    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String fechaNacimiento;   // String para simplificar serialización XML
    private String direccion;
    private String telefono;
    private String email;
    private String tipoCliente;
    private String estado;
    private String fechaRegistro;
}
