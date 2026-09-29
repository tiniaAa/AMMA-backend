package Amma.e_comerce.dto;

import java.math.BigDecimal;
import java.util.List;

public record ProductoResponseDto(
	    Long id, 
	    String nombre, 
	    String descripcion, 
	    BigDecimal precio, 
	    String categoria, 
	    List<String> rutasImagenes, // Cambiado a Lista
	    Boolean activo,
        List<VariacionDto> variaciones
	) {}