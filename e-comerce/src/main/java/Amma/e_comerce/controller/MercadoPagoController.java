package Amma.e_comerce.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import Amma.e_comerce.services.MercadoPagoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/mercadopago")
@RequiredArgsConstructor
public class MercadoPagoController {

    private final MercadoPagoService mercadoPagoService;

    public record ConfirmarPagoRequest(Long ordenId, Long paymentId) {
    }

    // El front pide el link de pago para una orden ya creada
    @PostMapping("/crear-preferencia/{idOrden}")
    public ResponseEntity<Map<String, String>> generarLinkDePago(@PathVariable Long idOrden) {
        String urlDePago = mercadoPagoService.crearPreferencia(idOrden);
        return ResponseEntity.ok(Map.of("url", urlDePago));
    }

    // El front confirma el pago al volver de Mercado Pago.
    // El backend NO confía en lo que manda el front: consulta el pago directo a Mercado Pago.
    @PostMapping("/confirmar-pago")
    public ResponseEntity<Map<String, String>> confirmarPago(@RequestBody ConfirmarPagoRequest request) {
        if (request.ordenId() == null || request.paymentId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Faltan ordenId o paymentId");
        }
        String estado = mercadoPagoService.confirmarPago(request.ordenId(), request.paymentId());
        return ResponseEntity.ok(Map.of("estado", estado));
    }

    // Estado guardado de la orden (PENDIENTE, PAGADO, RECHAZADO, REEMBOLSADO)
    @GetMapping("/estado/{idOrden}")
    public ResponseEntity<Map<String, String>> estado(@PathVariable Long idOrden) {
        return ResponseEntity.ok(Map.of("estado", mercadoPagoService.obtenerEstado(idOrden)));
    }

    // Webhook: Mercado Pago llama acá cuando cambia el estado de un pago.
    // Solo funciona con una URL pública (producción o ngrok).
    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(
            @RequestParam(name = "data.id", required = false) String dataId,
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "topic", required = false) String topic,
            @RequestParam(name = "id", required = false) String id,
            @RequestBody(required = false) Map<String, Object> body) {

        String tipo = type != null ? type : topic;
        if (tipo == null && body != null && body.get("type") != null) {
            tipo = String.valueOf(body.get("type"));
        }
        if (!"payment".equals(tipo)) {
            return ResponseEntity.ok().build();
        }

        String paymentId = dataId != null ? dataId : id;
        if (paymentId == null && body != null && body.get("data") instanceof Map<?, ?> data
                && data.get("id") != null) {
            paymentId = String.valueOf(data.get("id"));
        }
        if (paymentId == null || !paymentId.matches("\\d+")) {
            return ResponseEntity.ok().build();
        }

        try {
            mercadoPagoService.procesarNotificacion(Long.valueOf(paymentId));
            return ResponseEntity.ok().build();
        } catch (ResponseStatusException e) {
            log.warn("Webhook del pago {} no procesado: {}", paymentId, e.getReason());
            // 4xx: no tiene sentido reintentar. 5xx: que Mercado Pago reintente.
            return e.getStatusCode().is4xxClientError()
                    ? ResponseEntity.ok().build()
                    : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
