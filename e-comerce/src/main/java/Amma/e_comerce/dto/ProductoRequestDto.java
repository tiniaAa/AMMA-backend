package Amma.e_comerce.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoRequestDto {
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private int stock;
    private String categoria;
    private String rutaImagen;
    // No hay ID, lo genera PostgreSQL.
}
