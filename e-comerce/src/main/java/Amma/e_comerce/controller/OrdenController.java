package Amma.e_comerce.controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Amma.e_comerce.dto.OrdenRequestDto;
import Amma.e_comerce.dto.OrdenResponseDto;
import Amma.e_comerce.services.OrdenService;
import lombok.RequiredArgsConstructor;

@RestController // Define que esta clase recibe peticiones y devuelve datos
@RequestMapping("/api/ordenes")
@RequiredArgsConstructor
//@CrossOrigin(origins = "http://localhost:5173") // Esencial para evitar el bloqueo del navegador[cite: 1]
public class OrdenController {

    private final OrdenService ordenService;

    @PostMapping("/comprar")
    public ResponseEntity<OrdenResponseDto> procesarCompra(@RequestBody OrdenRequestDto request) {
        OrdenResponseDto respuesta = ordenService.procesarCompra(request);
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }
}