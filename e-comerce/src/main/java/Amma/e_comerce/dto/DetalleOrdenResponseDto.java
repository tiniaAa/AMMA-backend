package Amma.e_comerce.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleOrdenResponseDto {
    private String nombreProducto;
    private int cantidad;
    private BigDecimal precioUnitario; // Este es el precio que se congeló
    private BigDecimal subtotal; // (cantidad * precioUnitario)
}