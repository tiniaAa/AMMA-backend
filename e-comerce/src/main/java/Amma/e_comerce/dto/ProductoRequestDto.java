package Amma.e_comerce.dto;

import java.math.BigDecimal;

public record ProductoRequestDto(
    String nombre, 
    String descripcion, 
    BigDecimal precio, 
    int stock, 
    String categoria, 
    String rutaImagen
) {}