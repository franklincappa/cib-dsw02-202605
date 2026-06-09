package com.ventastech.catalogo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventario")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación 1 a 1 con producto
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false, unique = true)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Producto producto;

    @Min(0)
    @Column(name = "stock_actual", nullable = false)
    private Integer stockActual = 0;

    @Min(0)
    @Column(name = "stock_minimo", nullable = false)
    private Integer stockMinimo = 0;

    @Min(0)
    @Column(name = "stock_maximo", nullable = false)
    private Integer stockMaximo = 9999;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist @PreUpdate
    protected void onUpdate() { fechaActualizacion = LocalDateTime.now(); }
}