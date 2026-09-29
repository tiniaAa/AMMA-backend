package Amma.e_comerce.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "variaciones", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"producto_id", "talle", "color"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Variacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(name = "talle", nullable = false)
    private String talle;

    @Column(name = "color", nullable = false)
    private String color;

    @Column(name = "stock", nullable = false)
    private int stock;

    @Column(nullable = false, columnDefinition = "boolean default true")
    private Boolean activo = true;
}