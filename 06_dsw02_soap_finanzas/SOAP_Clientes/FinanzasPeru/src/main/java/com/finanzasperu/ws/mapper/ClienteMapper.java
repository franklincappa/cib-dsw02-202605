package com.finanzasperu.ws.mapper;

import com.finanzasperu.ws.dto.ClienteDTO;
import com.finanzasperu.ws.model.Cliente;
import org.mapstruct.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Mapper entre Cliente (entidad JPA) y ClienteDTO (contrato SOAP).
 *
 * Patron Mapper con MapStruct:
 *  - MapStruct genera el codigo de conversion en tiempo de compilacion (no reflection).
 *  - Es mas rapido que frameworks como ModelMapper.
 *  - @Mapping permite personalizar campos con nombres distintos o transformaciones.
 */
@Mapper(componentModel = "spring")  // Spring inyecta el mapper como @Bean
public interface ClienteMapper {

    DateTimeFormatter FMT_DATE     = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    DateTimeFormatter FMT_DATETIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @Mapping(target = "fechaNacimiento", expression = "java(formatDate(cliente.getFechaNacimiento()))")
    @Mapping(target = "fechaRegistro",   expression = "java(formatDateTime(cliente.getFechaRegistro()))")
    ClienteDTO toDTO(Cliente cliente);

    @Mapping(target = "fechaNacimiento", expression = "java(parseDate(dto.getFechaNacimiento()))")
    @Mapping(target = "fechaRegistro",   ignore = true)   // se asigna en @PrePersist
    @Mapping(target = "idTipoClienteRef", ignore = true)
    Cliente toEntity(ClienteDTO dto);

    List<ClienteDTO> toDTOList(List<Cliente> clientes);

    // ── Metodos de conversion de fechas ─────────────────────────────────────
    default String formatDate(LocalDate date) {
        return date != null ? date.format(FMT_DATE) : null;
    }

    default String formatDateTime(LocalDateTime dt) {
        return dt != null ? dt.format(FMT_DATETIME) : null;
    }

    default LocalDate parseDate(String date) {
        if (date == null || date.isBlank()) return null;
        return LocalDate.parse(date, FMT_DATE);
    }
}
