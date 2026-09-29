package Amma.e_comerce.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import Amma.e_comerce.model.DetalleOrden;
import Amma.e_comerce.model.Orden;
import Amma.e_comerce.repository.OrdenRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Integración con Mercado Pago Checkout Pro usando la API REST directamente
 * (mismo pedido que se probó con curl), sin pasar por el SDK de Java.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MercadoPagoService {

    public static final String PENDIENTE = "PENDIENTE";
    public static final String PAGADO = "PAGADO";
    public static final String RECHAZADO = "RECHAZADO";
    public static final String REEMBOLSADO = "REEMBOLSADO";

    private static final String API_BASE = "https://api.mercadopago.com";
    private static final ParameterizedTypeReference<Map<String, Object>> MAP_TYPE = new ParameterizedTypeReference<>() {
    };

    private final OrdenRepository ordenRepository;
    private final Amma.e_comerce.services.OrdenService ordenService; // <--- AGREGAR
    private final Amma.e_comerce.services.EmailService emailService; 
    @Value("${mercadopago.access-token}")
    private String accessToken;

    // URL del front a la que vuelve el cliente. En producción: https://tudominio.com
    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    // URL pública del webhook. Vacía en local (se usa la confirmación desde el front).
    @Value("${mercadopago.notification-url:}")
    private String notificationUrl;

    private RestClient http;

    /** Datos del pago que nos interesan. */
    private record PagoMp(Long id, String status, String externalReference, BigDecimal monto) {
    }

    @PostConstruct
    public void init() {
        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalStateException("Falta la propiedad mercadopago.access-token");
        }
        String token = accessToken.trim();
        this.http = RestClient.builder()
                .baseUrl(API_BASE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build();

        // El último tramo del token es el id de la cuenta vendedora: sirve para comparar sin exponer el token
        String[] partes = token.split("-");
        log.info("Mercado Pago configurado: token de {} caracteres, cuenta {}, frontend {}, webhook '{}'",
                token.length(), partes[partes.length - 1], frontendUrl, notificationUrl);
    }

    // ------------------------------------------------------------------
    // 1. Crear la preferencia (link de pago)
    // ------------------------------------------------------------------
    @Transactional(readOnly = true)
    public String crearPreferencia(Long idOrden) {
        Orden orden = buscarOrden(idOrden);

        if (PAGADO.equals(estadoDe(orden))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La orden ya fue pagada");
        }

        List<Map<String, Object>> items = new ArrayList<>();
        for (DetalleOrden detalle : orden.getDetalles()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("title", detalle.getNombreProducto());
            item.put("quantity", detalle.getCantidad());
            item.put("unit_price", detalle.getPrecioUnitario());
            item.put("currency_id", "ARS");
            items.add(item);
        }
        if (items.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La orden no tiene productos");
        }

        // Envío a domicilio: se cobra como un ítem más (el costo quedó guardado en la orden al comprarla)
        BigDecimal costoEnvio = orden.getCostoEnvio();
        if (costoEnvio != null && costoEnvio.signum() > 0) {
            Map<String, Object> envio = new LinkedHashMap<>();
            envio.put("title", "Envío a domicilio");
            envio.put("quantity", 1);
            envio.put("unit_price", costoEnvio);
            envio.put("currency_id", "ARS");
            items.add(envio);
        }

        String base = frontendUrl.endsWith("/") ? frontendUrl.substring(0, frontendUrl.length() - 1) : frontendUrl;

        Map<String, Object> backUrls = new LinkedHashMap<>();
        backUrls.put("success", base + "/check?estado=exito");
        backUrls.put("pending", base + "/check?estado=pendiente");
        backUrls.put("failure", base + "/check?estado=fallo");

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("items", items);
        body.put("back_urls", backUrls);
        body.put("external_reference", String.valueOf(orden.getId()));

        // Mercado Pago rechaza auto_return si las back_urls son http://localhost.
        // Solo lo activamos cuando el front está en https (producción o ngrok).
        if (base.startsWith("https://")) {
            body.put("auto_return", "approved");
        }
        if (notificationUrl != null && !notificationUrl.isBlank()) {
            body.put("notification_url", notificationUrl.trim());
        }

        // Este log NO incluye el token: se puede copiar y pegar sin problema
        log.info("Creando preferencia en Mercado Pago: {}", body);

        try {
            Map<String, Object> respuesta = http.post()
                    .uri("/checkout/preferences")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(MAP_TYPE);

            Object initPoint = respuesta == null ? null : respuesta.get("init_point");
            if (initPoint == null) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Mercado Pago no devolvió el link de pago");
            }
            return initPoint.toString();

        } catch (RestClientResponseException e) {
            log.error("Mercado Pago rechazó la preferencia. status={} body={}",
                    e.getStatusCode().value(), e.getResponseBodyAsString());
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Mercado Pago rechazó la petición");
        } catch (RestClientException e) {
            log.error("No se pudo comunicar con Mercado Pago", e);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error al comunicarse con Mercado Pago");
        }
    }

    // ------------------------------------------------------------------
    // 2. Confirmación desde el front (al volver de Mercado Pago)
    //    Funciona en localhost, no necesita URL pública.
    // ------------------------------------------------------------------
    @Transactional
    public String confirmarPago(Long idOrden, Long paymentId) {
        Orden orden = buscarOrden(idOrden);
        PagoMp pago = obtenerPago(paymentId);

        if (!String.valueOf(orden.getId()).equals(pago.externalReference())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El pago no corresponde a esta orden");
        }

        aplicarPago(orden, pago);
        return estadoDe(orden);
    }

    // ------------------------------------------------------------------
    // 3. Webhook (Mercado Pago avisa al backend, incluso si el cliente cierra la pestaña)
    // ------------------------------------------------------------------
    @Transactional
    public void procesarNotificacion(Long paymentId) {
        PagoMp pago = obtenerPago(paymentId);
        String referencia = pago.externalReference();

        if (referencia == null || !referencia.matches("\\d+")) {
            log.info("Pago {} sin external_reference válido, se ignora", paymentId);
            return;
        }

        ordenRepository.findById(Long.valueOf(referencia)).ifPresentOrElse(
                orden -> aplicarPago(orden, pago),
                () -> log.warn("Pago {} referencia una orden inexistente: {}", paymentId, referencia));
    }

    // ------------------------------------------------------------------
    // 4. Consultar el estado guardado de una orden
    // ------------------------------------------------------------------
    @Transactional(readOnly = true)
    public String obtenerEstado(Long idOrden) {
        return estadoDe(buscarOrden(idOrden));
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------
    private void aplicarPago(Orden orden, PagoMp pago) {
        String nuevoEstado = traducirEstado(pago.status());
        String estadoActual = estadoDe(orden);
        if (PAGADO.equals(nuevoEstado) && !montoCoincide(orden, pago)) {
            log.warn("Monto pagado ({}) distinto al de la orden {}", pago.monto(), orden.getId());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El monto pagado no coincide con la orden");
        }
        if (PAGADO.equals(estadoActual) && !REEMBOLSADO.equals(nuevoEstado)) {
            return;
        }
        // --- SOLUCIÓN DE SEGURIDAD (PAGO TARDÍO) ---
        if ("VENCIDA".equals(estadoActual) && PAGADO.equals(nuevoEstado)) {
            orden.setEstadoPago("PAGADO_SIN_STOCK");
            orden.setPaymentId(String.valueOf(pago.id()));
            ordenRepository.save(orden);
            log.error("ALERTA CRÍTICA: Pago aprobado de Mercado Pago para la orden VENCIDA {}. Requiere reembolso manual o gestión con el cliente.", orden.getId());
            return; // Cortamos acá, no enviamos mail automático.
        }
        orden.setEstadoPago(nuevoEstado);
        orden.setPaymentId(String.valueOf(pago.id()));
        ordenRepository.save(orden);
        log.info("Orden {} -> {} (pago {})", orden.getId(), nuevoEstado, pago.id());
        // --- DISPARO DEL MAIL AL CONFIRMAR PAGO ---
        if (PENDIENTE.equals(estadoActual) && PAGADO.equals(nuevoEstado)) {
            emailService.enviarCorreosDeCompra(ordenService.mapearOrdenAResponseDto(orden));
        }
    }

    private String traducirEstado(String statusMp) {
        if (statusMp == null) {
            return PENDIENTE;
        }
        return switch (statusMp) {
            case "approved" -> PAGADO;
            case "rejected", "cancelled" -> RECHAZADO;
            case "refunded", "charged_back" -> REEMBOLSADO;
            default -> PENDIENTE; // pending, in_process, authorized, in_mediation
        };
    }

    private boolean montoCoincide(Orden orden, PagoMp pago) {
        // totalPagar = productos + envío, calculado por el backend al crear la orden
        return pago.monto() != null
                && orden.getTotalPagar() != null
                && orden.getTotalPagar().compareTo(pago.monto()) == 0;
    }

    private PagoMp obtenerPago(Long paymentId) {
        try {
            Map<String, Object> p = http.get()
                    .uri("/v1/payments/{id}", paymentId)
                    .retrieve()
                    .body(MAP_TYPE);

            if (p == null) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Respuesta vacía de Mercado Pago");
            }

            Object id = p.get("id");
            Object status = p.get("status");
            Object referencia = p.get("external_reference");
            Object monto = p.get("transaction_amount");

            return new PagoMp(
                    id instanceof Number n ? n.longValue() : paymentId,
                    status == null ? null : status.toString(),
                    referencia == null ? null : referencia.toString(),
                    monto == null ? null : new BigDecimal(monto.toString()));

        } catch (RestClientResponseException e) {
            log.error("Error consultando el pago {}. status={} body={}",
                    paymentId, e.getStatusCode().value(), e.getResponseBodyAsString());
            if (e.getStatusCode().value() == 404) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pago no encontrado");
            }
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "No se pudo consultar el pago");
        } catch (RestClientException e) {
            log.error("No se pudo comunicar con Mercado Pago para el pago {}", paymentId, e);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "No se pudo consultar el pago");
        }
    }

    private Orden buscarOrden(Long idOrden) {
        return ordenRepository.findById(idOrden)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Orden no encontrada"));
    }

    private String estadoDe(Orden orden) {
        return orden.getEstadoPago() == null ? PENDIENTE : orden.getEstadoPago();
    }
}
