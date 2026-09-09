package Amma.e_comerce.dto;

import java.math.BigDecimal;

public record DetalleOrdenResponseDto(
	    String nombreProducto, 
	    int cantidad, 
	    BigDecimal precioUnitario, 
	    BigDecimal subtotal
	) {}
