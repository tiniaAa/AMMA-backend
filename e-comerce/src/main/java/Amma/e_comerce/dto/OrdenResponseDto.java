package Amma.e_comerce.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdenResponseDto {
    private Long id;
    private BigDecimal totalPagar; // El total calculado de forma segura
    private String tipoEnvio;
    private LocalDateTime fechaCompra;
    // Datos de envío adjuntos para el recibo
    private String compradorNombre;
    private String compradorDireccion;
    
    private List<DetalleOrdenResponseDto> detalles;
}