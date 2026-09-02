package Amma.e_comerce.services;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public OrdenResponseDto procesarCompra(OrdenRequestDto request) {
    	if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("No se puede procesar una orden sin productos.");
        }
    	Orden orden = new Orden();
    	orden.setCompradorApellido(request.getCompradorApellido());
    	orden.setCompradorCiudad(request.getCompradorCiudad());
    	orden.setCompradorCp(request.getCompradorCp());
    	orden.setCompradorDireccion(request.getCompradorDireccion());
    	orden.setCompradorNombre(request.getCompradorNombre());
    	orden.setCompradorProvincia(request.getCompradorProvincia());
    	
    	BigDecimal totalCalculado = BigDecimal.ZERO;
    	
    	for(ItemCompraDto item : request.getItems()) {
    		Producto producto = productoRepository.findById(item.getProductoId())
    				.orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + item.getProductoId()));
    		if(producto.getStock()<item.getCantidad()) {
    			throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre());
    		}
    		
    		producto.setStock(producto.getStock()-item.getCantidad());
    		
    		DetalleOrden detalle = new DetalleOrden();
    		
    		detalle.setCantidad(item.getCantidad());
    		detalle.setPrecioUnitario(producto.getPrecio());
    		detalle.setNombreProducto(producto.getNombre());
    		detalle.setProducto(producto);
    		detalle.setOrden(orden);

            orden.getDetalles().add(detalle);
    	}
    	
    	// 1. Crear una nueva entidad Orden vacía
        // 2. Recorrer la lista de items del DTO de entrada (request.getItems())
    	// 3. Por cada item, ir a la base de datos (productoRepository.findById) para buscar el precio REAL
        // 4. Multiplicar precio REAL x cantidad solicitada
        // 5. Restar el stock del producto
        // 6. Generar el DetalleOrden y sumarlo al total de la Orden
        // 7. Guardar la Orden en base de datos (ordenRepository.save)
        // 8. Convertir el resultado a OrdenResponseDto y devolverlo
        
        return null; // Retorno temporal
    }
}