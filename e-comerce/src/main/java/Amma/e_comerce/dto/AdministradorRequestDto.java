package Amma.e_comerce.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // @Data incluye getters, setters, toString, equals y hashCode
@NoArgsConstructor
@AllArgsConstructor
public class AdministradorRequestDto {
    private String email;
    private String password; 
}