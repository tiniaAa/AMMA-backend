package Amma.e_comerce.controller;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Amma.e_comerce.model.Orden;
import Amma.e_comerce.repository.OrdenRepository;
import Amma.e_comerce.services.MercadoPagoService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/mercadopago")
@RequiredArgsConstructor
public class MercadoPagoController {

    private final MercadoPagoService mercadoPagoService;
    private final OrdenRepository ordenRepository;

    @PostMapping("/crear-preferencia/{idOrden}")
    public ResponseEntity<Map<String, String>> generarLinkDePago(@PathVariable Long idOrden) {
        
        // 1. Buscamos la orden recién creada en tu base de datos
        Orden orden = ordenRepository.findById(idOrden)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));
        
        // 2. Le pedimos el link a Mercado Pago
        String urlDePago = mercadoPagoService.crearPreferencia(orden);
        
        // 3. Lo devolvemos en formato JSON para que React lo lea fácil
        return ResponseEntity.ok(Map.of("url", urlDePago));
    }
}