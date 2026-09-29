package Amma.e_comerce.dto;

import java.math.BigDecimal;
public record DetalleOrdenResponseDto(
	    String nombreProducto, 
        String talle,
        String color,
	    int cantidad, 
	    BigDecimal precioUnitario, 
	    BigDecimal subtotal
	) {}
