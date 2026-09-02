package Amma.e_comerce.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdenRequestDto {
    private String tipoEnvio;
    private String compradorNombre;
    private String compradorApellido;
    private String compradorDireccion;
    private String compradorCiudad;
    private String compradorCp;
    private String compradorProvincia;
    
    private List<ItemCompraDto> items; 
}