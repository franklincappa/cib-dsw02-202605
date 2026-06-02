package com.finanzasperu.ws.dto;

import jakarta.xml.bind.annotation.*;
        import lombok.*;
        import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ListaClientesDTO")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ListaClientesDTO {

    @XmlElement(name = "cliente")
    private List<ClienteDTO> clientes;
}