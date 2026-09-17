package Amma.e_comerce.services;

import Amma.e_comerce.dto.AdministradorRequestDto;
import Amma.e_comerce.dto.AdministradorResponseDto;
import Amma.e_comerce.model.Administrador;
import Amma.e_comerce.repository.AdministradorRepository;
import lombok.RequiredArgsConstructor;

// --- IMPORTACIONES FALTANTES ---
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdministradorService {

    private final AdministradorRepository administradorRepository;
    
    // Inyectamos el gestor de autenticación de Spring Security
    private final AuthenticationManager authenticationManager;
    
    // Inyectamos el servicio JWT que creamos en el paso anterior
    private final JwtService jwtService; 
    
    // Inyectamos el servicio que busca al usuario para la seguridad
    private final UserDetailsService userDetailsService;

    public AdministradorResponseDto login(AdministradorRequestDto request) {
        
        // 1. Spring Security compara automáticamente la contraseña plana con el Hash de PostgreSQL
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        // 2. Si llegamos hasta acá, es porque las credenciales eran correctas. Buscamos al admin.
        Administrador admin = administradorRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("El correo ingresado no existe."));

        // 3. Cargamos los detalles del usuario según el estándar de Spring Security
        UserDetails userDetails = userDetailsService.loadUserByUsername(admin.getEmail());
        
        // 4. Generamos el Token JWT (la pulsera VIP)
        String jwtToken = jwtService.generateToken(userDetails);

        // 5. Devolvemos la respuesta incluyendo el Token
        return new AdministradorResponseDto(admin.getId(), admin.getEmail(), jwtToken);
    }
}