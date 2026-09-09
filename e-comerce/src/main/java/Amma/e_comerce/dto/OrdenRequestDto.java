package Amma.e_comerce.dto;

import java.util.List;

public record OrdenRequestDto(
	    String tipoEnvio, 
	    String compradorNombre, 
	    String compradorApellido, 
	    String compradorDireccion, 
	    String compradorCiudad, 
	    String compradorCp, 
	    String compradorProvincia, 
	    List<ItemCompraDto> items
	) {}
