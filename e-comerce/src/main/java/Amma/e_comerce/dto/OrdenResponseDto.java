package Amma.e_comerce.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrdenResponseDto(
	    Long id, 
	    BigDecimal totalPagar, 
	    String tipoEnvio, 
	    LocalDateTime fechaCompra, 
	    String compradorNombre, 
	    String compradorDireccion, 
	    List<DetalleOrdenResponseDto> detalles
	) {}