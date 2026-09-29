package Amma.e_comerce.dto;

import java.math.BigDecimal;
import java.util.List;

public record ProductoRequestDto(
    String nombre, 
    String descripcion, 
    BigDecimal precio, 
    String categoria, 
    List<String> rutasImagenes, // Cambiado a Lista
    List<VariacionDto> variaciones
) {}