package Amma.e_comerce.dto;
public record VariacionDto(
    Long id,
    String talle,
    String color,
    int stock,
    Boolean activo
) {}