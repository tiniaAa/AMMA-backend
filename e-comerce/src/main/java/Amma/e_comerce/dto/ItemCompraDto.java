package Amma.e_comerce.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemCompraDto {
    private Long productoId;
    private int cantidad;
    // NADA DE PRECIOS ACÁ. El precio se calcula en el servidor.
}