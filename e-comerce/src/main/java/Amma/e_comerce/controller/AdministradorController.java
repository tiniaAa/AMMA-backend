package Amma.e_comerce.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Amma.e_comerce.dto.AdministradorRequestDto;
import Amma.e_comerce.dto.AdministradorResponseDto;
import Amma.e_comerce.services.AdministradorService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
//@CrossOrigin(origins = "http://localhost:5173") // Clave para evitar el bloqueo de CORS desde React
public class AdministradorController {

    private final AdministradorService administradorService;

    @PostMapping("/login")
    public ResponseEntity<AdministradorResponseDto> login(@RequestBody AdministradorRequestDto request) {        
        AdministradorResponseDto respuesta = administradorService.login(request);
        return ResponseEntity.ok(respuesta);
    }
}