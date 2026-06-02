package com.finanzasperu.ws.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad Cliente - mapea la tabla CLIENTES de FinanzasPeruDB.
 *
 * Patrones aplicados:
 *  - @Entity + @Table  -> Patron Repository (JPA)
 *  - Lombok            -> reduce boilerplate (getters, setters, constructors)
 *  - @Column(nullable) -> constraints a nivel JPA reflejan el esquema SQL
 */
@Entity
@Table(name = "CLIENTES")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder                    // permite: Cliente.builder().nombre("Juan").build()
@ToString(exclude = {"cuentas"})
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Long idCliente;

    @Column(name = "tipo_documento", nullable = false, length = 3)
    private String tipoDocumento;   // DNI, RUC, CE

    @Column(name = "nro_documento", nullable = false, unique = true, length = 15)
    private String nroDocumento;

    @Column(nullable = false, length = 100)
    private String nombres;

    @Column(name = "apellido_paterno", nullable = false, length = 60)
    private String apellidoPaterno;

    @Column(name = "apellido_materno", length = 60)
    private String apellidoMaterno;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(nullable = false, length = 200)
    private String direccion;

    @Column(nullable = false, length = 15)
    private String telefono;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "tipo_cliente", length = 20)
    private String tipoCliente;     // NATURAL, JURIDICO

    @Column(name = "estado", length = 10)
    private String estado;          // ACTIVO, INACTIVO, BLOQUEADO

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    @Column(name = "id_tipo_cliente_ref")
    private Long idTipoClienteRef;

    @PrePersist
    public void prePersist() {
        if (fechaRegistro == null) fechaRegistro = LocalDateTime.now();
        if (estado == null) estado = "ACTIVO";
    }
}
