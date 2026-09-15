package Amma.e_comerce.dto;

import java.math.BigDecimal;

public record ProductoResponseDto(
	    Long id, 
	    String nombre, 
	    String descripcion, 
	    BigDecimal precio, 
	    int stock, 
	    String categoria, 
	    String rutaImagen,
	    Boolean activo // <-- Agregamos este campo
	) {}