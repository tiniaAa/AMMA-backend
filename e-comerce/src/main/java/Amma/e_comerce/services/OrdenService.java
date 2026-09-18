package Amma.e_comerce.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import Amma.e_comerce.dto.DetalleOrdenResponseDto;
import Amma.e_comerce.dto.ItemCompraDto;
import Amma.e_comerce.dto.OrdenRequestDto;
import Amma.e_comerce.dto.OrdenResponseDto;
import Amma.e_comerce.model.DetalleOrden;
import Amma.e_comerce.model.Orden;
import Amma.e_comerce.model.Producto;
import Amma.e_comerce.repository.OrdenRepository;
import Amma.e_comerce.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrdenService {

    // Se inyectan como constantes. Lombok crea el constructor por vos.
    private final OrdenRepository ordenRepository;
    private final ProductoRepository productoRepository;
    private final EmailService emailService;
    @Transactional
    public OrdenResponseDto procesarCompra(OrdenRequestDto request) {
    	if (request.items() == null || request.items().isEmpty()) {
            throw new IllegalArgumentException("No se puede procesar una orden sin productos.");
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
    	
    	BigDecimal totalCalculado = BigDecimal.ZERO;
    	
    	for(ItemCompraDto item : request.items()) {
    		Producto producto = productoRepository.findById(item.productoId())
    				.orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + item.productoId()));
    		if(producto.getStock()<item.cantidad()) {
    			throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre());
    		}
    		
    		producto.setStock(producto.getStock()-item.cantidad());
    		
    		DetalleOrden detalle = new DetalleOrden();
    		
    		detalle.setCantidad(item.cantidad());
    		detalle.setPrecioUnitario(producto.getPrecio());
    		detalle.setNombreProducto(producto.getNombre());
    		detalle.setProducto(producto);
    		detalle.setOrden(orden);

            orden.getDetalles().add(detalle);
            
            BigDecimal subTotal  = producto.getPrecio().multiply(new BigDecimal(item.cantidad()));
            totalCalculado = totalCalculado.add(subTotal);
    	}
    	orden.setTotalPagar(totalCalculado);
    	Orden ordenGuardada = ordenRepository.save(orden);
    	OrdenResponseDto respuestaDto =mapearOrdenAResponseDto(ordenGuardada); 
        
    	emailService.enviarCorreosDeCompra(respuestaDto);
    	
    	return respuestaDto;
        
    }
    private OrdenResponseDto mapearOrdenAResponseDto(Orden orden) {
        // Mapeamos la lista de detalles instanciando el record de golpe
        List<DetalleOrdenResponseDto> detallesDto = orden.getDetalles().stream()
            .map(detalle -> new DetalleOrdenResponseDto(
                detalle.getNombreProducto(),
                detalle.getCantidad(),
                detalle.getPrecioUnitario(),
                detalle.getPrecioUnitario().multiply(new BigDecimal(detalle.getCantidad()))
            )).toList();

        return new OrdenResponseDto(
            orden.getId(),
            orden.getTotalPagar(),
            orden.getTipoEnvio(),
            orden.getFechaCompra(),
            orden.getCompradorNombre(),
            orden.getCompradorEmail(),
            orden.getCompradorDireccion(),
            orden.getCompradorTelefono(),
            detallesDto
        );
    }
}