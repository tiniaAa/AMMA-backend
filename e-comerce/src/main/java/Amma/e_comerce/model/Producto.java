package Amma.e_comerce.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="productos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Producto {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private Long id;
	
	@Column(name="nombre",nullable=false)
	private String nombre;
	
	@Column(name="descripcion")
	private String descripcion;
	
	@Column(name="precio",nullable=false)
	private BigDecimal precio;
	
	@Column(name="stock",nullable=false)
	private int stock;
	
	@Column(name="categoria",nullable=false)
	private String categoria;
	
	@Column(name="ruta_imagen")
	private String rutaImagen;
}
