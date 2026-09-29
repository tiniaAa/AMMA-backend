package Amma.e_comerce.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import Amma.e_comerce.dto.DetalleOrdenResponseDto;
import Amma.e_comerce.dto.ItemCompraDto;
import Amma.e_comerce.dto.OrdenRequestDto;
import Amma.e_comerce.dto.OrdenResponseDto;
import Amma.e_comerce.model.DetalleOrden;
import Amma.e_comerce.model.Orden;
import Amma.e_comerce.model.Producto;
import Amma.e_comerce.model.Variacion;
import Amma.e_comerce.repository.OrdenRepository;
import Amma.e_comerce.repository.VariacionRepository;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
@Service
@RequiredArgsConstructor
public class OrdenService {

    public static final String ENVIO_DOMICILIO = "domicilio";
    public static final String RETIRO_LOCAL = "local";
    public static final String PENDIENTE = "PENDIENTE";

    private final OrdenRepository ordenRepository;
    private final VariacionRepository variacionRepository; 
    private final EmailService emailService;

    @Value("${tienda.costo-envio:2500}")
    private BigDecimal costoEnvio;

    public BigDecimal getCostoEnvio() {
        return costoEnvio;
    }

    @Transactional
    public OrdenResponseDto procesarCompra(OrdenRequestDto request) {
        if (request.items() == null || request.items().isEmpty()) {
            throw new IllegalArgumentException("No se puede procesar una orden sin productos.");
        }
        if (!ENVIO_DOMICILIO.equals(request.tipoEnvio()) && !RETIRO_LOCAL.equals(request.tipoEnvio())) {
            throw new IllegalArgumentException("Tipo de envío inválido.");
        }

        Orden orden = new Orden();
        orden.setCompradorApellido(request.compradorApellido());
        orden.setCompradorCiudad(request.compradorCiudad());
        orden.setCompradorCp(request.compradorCp());
        orden.setCompradorEmail(request.compradorEmail());
        orden.setCompradorDireccion(request.compradorDireccion());
        orden.setCompradorNombre(request.compradorNombre());
        orden.setCompradorProvincia(request.compradorProvincia());
        orden.setTipoEnvio(request.tipoEnvio());
        orden.setCompradorTelefono(request.compradorTelefono());
        
        // Asignamos el método de pago que nos llega del front
        orden.setMetodoPago(request.metodoPago() != null ? request.metodoPago() : "efectivo");
        orden.setEstadoPago(PENDIENTE);

        BigDecimal totalCalculado = BigDecimal.ZERO;

        for (ItemCompraDto item : request.items()) {
            // Buscamos la VARIACIÓN (SKU) en vez del producto genérico
            Variacion variacion = variacionRepository.findById(item.variacionId())
                    .orElseThrow(() -> new RuntimeException("Variación no encontrada con ID: " + item.variacionId()));
            
            Producto producto = variacion.getProducto();
            
            if (!producto.getActivo() || !variacion.getActivo()) {
                throw new RuntimeException("El producto " + producto.getNombre() + " no está disponible.");
            }

            if (variacion.getStock() < item.cantidad()) {
                throw new RuntimeException("Stock insuficiente para: " + producto.getNombre() + " - " + variacion.getTalle() + " " + variacion.getColor());
            }

            // DESCONTAMOS EL STOCK (Reservamos la cantidad elegida)
            variacion.setStock(variacion.getStock() - item.cantidad());

            DetalleOrden detalle = new DetalleOrden();
            detalle.setCantidad(item.cantidad());
            detalle.setPrecioUnitario(producto.getPrecio());
            
            // Guardamos la foto (snapshot) de lo que compró
            detalle.setNombreProducto(producto.getNombre());
            detalle.setTalle(variacion.getTalle());
            detalle.setColor(variacion.getColor());
            
            detalle.setVariacion(variacion);
            detalle.setOrden(orden);

            orden.getDetalles().add(detalle);

            BigDecimal subTotal = producto.getPrecio().multiply(new BigDecimal(item.cantidad()));
            totalCalculado = totalCalculado.add(subTotal);
        }

        BigDecimal envio = ENVIO_DOMICILIO.equals(request.tipoEnvio()) ? costoEnvio : BigDecimal.ZERO;
        orden.setCostoEnvio(envio);
        orden.setTotalPagar(totalCalculado.add(envio));

        Orden ordenGuardada = ordenRepository.save(orden);
        OrdenResponseDto respuestaDto = mapearOrdenAResponseDto(ordenGuardada);

        // Si es efectivo, el mail se manda ahora. Si es MP, lo frenamos hasta que esté pagado.
        if ("efectivo".equalsIgnoreCase(orden.getMetodoPago())) {
            emailService.enviarCorreosDeCompra(respuestaDto);
        }

        return respuestaDto;
    }

    public OrdenResponseDto mapearOrdenAResponseDto(Orden orden) {
        List<DetalleOrdenResponseDto> detallesDto = orden.getDetalles().stream()
                .map(detalle -> new DetalleOrdenResponseDto(
                        detalle.getNombreProducto(),
                        detalle.getTalle(),
                        detalle.getColor(),
                        detalle.getCantidad(),
                        detalle.getPrecioUnitario(),
                        detalle.getPrecioUnitario().multiply(new BigDecimal(detalle.getCantidad()))))
                .toList();

        return new OrdenResponseDto(
                orden.getId(),
                orden.getTotalPagar(),
                orden.getTipoEnvio(),
                orden.getFechaCompra(),
                orden.getCompradorNombre(),
                orden.getCompradorEmail(),
                orden.getCompradorDireccion(),
                orden.getCompradorTelefono(),
                detallesDto);
    }
    @Transactional
    public void liberarReservasVencidas(int minutosReserva) {
        java.time.LocalDateTime limite = java.time.LocalDateTime.now().minusMinutes(minutosReserva);
        List<Orden> vencidas = ordenRepository.findByMetodoPagoAndEstadoPagoAndFechaCompraBefore(
                "mercadopago", "PENDIENTE", limite
            );
        for (Orden orden : vencidas) {
            orden.setEstadoPago("VENCIDA"); // Cambiamos el estado a vencida
            
            // Devolvemos el stock de cada variación
            for (DetalleOrden detalle : orden.getDetalles()) {
                Variacion variacion = detalle.getVariacion();
                variacion.setStock(variacion.getStock() + detalle.getCantidad());
                variacionRepository.save(variacion);
            }
            ordenRepository.save(orden);
        }
    }
    
}