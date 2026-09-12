package Amma.e_comerce.services;

import java.util.List;

import org.springframework.stereotype.Service;

import Amma.e_comerce.dto.ProductoRequestDto;
import Amma.e_comerce.dto.ProductoResponseDto;
import Amma.e_comerce.model.Producto;
import Amma.e_comerce.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductoService {
	private final ProductoRepository productoRepository;
	
	public ProductoResponseDto crear(ProductoRequestDto request) {
		Producto producto = new Producto();
		
		producto.setCategoria(request.categoria());
		producto.setDescripcion(request.descripcion());
		producto.setNombre(request.nombre());
		producto.setPrecio(request.precio());
		producto.setRutaImagen(request.rutaImagen());
		producto.setStock(request.stock());
		
		Producto guardado = productoRepository.save(producto);
		
		
		
		return mapearResponseDto(guardado);
	}
	public List<ProductoResponseDto> ObtenerTodos(){
		return
				productoRepository.findAll().stream()
				.map(this::mapearResponseDto)
				.toList();
				
	}
	public ProductoResponseDto mapearResponseDto (Producto producto) {
		return new ProductoResponseDto(
				producto.getId(),
				producto.getNombre(),
				producto.getDescripcion(),
				producto.getPrecio(),
				producto.getStock(),
				producto.getCategoria(),
				producto.getRutaImagen()
				);
	}
	
	
}
