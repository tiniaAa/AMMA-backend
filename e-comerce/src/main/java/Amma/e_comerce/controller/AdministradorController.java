package Amma.e_comerce.controller;


import Amma.e_comerce.dto.AdministradorRequestDto;
import Amma.e_comerce.dto.AdministradorResponseDto;
import Amma.e_comerce.services.AdministradorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdministradorController {

    private final AdministradorService administradorService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AdministradorRequestDto request) {
        try {
            System.out.println(">>> RECIBIENDO PETICIÓN DE LOGIN PARA: " + request.email());
            System.out.println(">>> HASH CORRECTO PARA ESTA CLAVE: " + new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode(request.password()));
            AdministradorResponseDto respuesta = administradorService.login(request);
            System.out.println(">>> LOGIN EXITOSO");
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            System.out.println(">>> ERROR DETECTADO EN EL LOGIN:");
            e.printStackTrace(); // Imprime la sangre en la consola de Eclipse
            return ResponseEntity.status(500).body("El error real es: " + e.getMessage());
        }
    }
}