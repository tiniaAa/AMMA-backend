package Amma.e_comerce.services;

import org.springframework.stereotype.Service;

import Amma.e_comerce.dto.AdministradorRequestDto;
import Amma.e_comerce.dto.AdministradorResponseDto;
import Amma.e_comerce.model.Administrador;
import Amma.e_comerce.repository.AdministradorRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdministradorService {

    private final AdministradorRepository administradorRepository;

    public AdministradorResponseDto login(AdministradorRequestDto request) {
        
        Administrador admin = administradorRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("El correo ingresado no existe."));

        // Comparamos contraseñas (Simplificado para esta etapa de desarrollo)
        if (!admin.getPasswordHash().equals(request.password())) {
            throw new RuntimeException("Contraseña incorrecta.");
        }

        // Si pasa la validación, devolvemos el DTO limpio sin la contraseña
        return new AdministradorResponseDto(admin.getId(), admin.getEmail());
    }
}