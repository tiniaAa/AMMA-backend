package Amma.e_comerce.services;

import java.util.List;
import org.springframework.stereotype.Service;

import Amma.e_comerce.dto.ProductoRequestDto;
import Amma.e_comerce.dto.ProductoResponseDto;
import Amma.e_comerce.dto.VariacionDto;
import Amma.e_comerce.model.Producto;
import Amma.e_comerce.model.Variacion;
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
        producto.setRutasImagenes(request.rutasImagenes());
        
        actualizarVariaciones(producto, request.variaciones());
        
        Producto guardado = productoRepository.save(producto);
        return mapearResponseDto(guardado);
    }

    public List<ProductoResponseDto> ObtenerTodos(){
        return productoRepository.findByActivoTrue().stream()
                .map(this::mapearResponseDto)
                .toList();
    }

    public List<ProductoResponseDto> obtenerPorCategoria(String categoria) {
    	return productoRepository.findByCategoriaIgnoreCaseAndActivoTrue(categoria).stream()
                .map(this::mapearResponseDto)
                .toList();
    }

    public List<ProductoResponseDto> obtenerTodosAdmin(){
        return productoRepository.findAll().stream()
                .map(this::mapearResponseDto)
                .toList();
    }

    public ProductoResponseDto obtenerPorId(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado en la base de datos"));
        return mapearResponseDto(producto);
    }

    public ProductoResponseDto actualizar(Long id, ProductoRequestDto request) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado para actualizar"));
        
        producto.setCategoria(request.categoria());
        producto.setDescripcion(request.descripcion());
        producto.setNombre(request.nombre());
        producto.setPrecio(request.precio());
        producto.setRutasImagenes(request.rutasImagenes());
        
        actualizarVariaciones(producto, request.variaciones());
        
        Producto actualizado = productoRepository.save(producto);
        return mapearResponseDto(actualizado);
    }

    public void eliminar(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("El producto no existe en la base de datos"));
        producto.setActivo(false);
        productoRepository.save(producto);
    }

    public void restaurar(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("El producto no existe en la base de datos"));
        producto.setActivo(true);
        productoRepository.save(producto);
    }
    
    // --- LÓGICA DE ACTUALIZACIÓN DE VARIACIONES (Mantiene las compras pasadas intactas) ---
    private void actualizarVariaciones(Producto producto, List<VariacionDto> variacionesDto) {
        if (variacionesDto == null) return;

        // Por precaución marcamos todas como inactivas
        producto.getVariaciones().forEach(v -> v.setActivo(false));

        for (VariacionDto dto : variacionesDto) {
            Variacion existente = producto.getVariaciones().stream()
                    .filter(v -> v.getId() != null && v.getId().equals(dto.id()))
                    .findFirst()
                    .orElse(null);

            if (existente != null) {
                existente.setTalle(dto.talle());
                existente.setColor(dto.color());
                existente.setStock(dto.stock());
                existente.setActivo(true); 
            } else {
                Variacion nueva = new Variacion();
                nueva.setTalle(dto.talle());
                nueva.setColor(dto.color());
                nueva.setStock(dto.stock());
                nueva.setActivo(true);
                nueva.setProducto(producto);
                producto.getVariaciones().add(nueva);
            }
        }
    }

    // --- MAPEO ---
    public ProductoResponseDto mapearResponseDto (Producto producto) {
        List<VariacionDto> variacionesDto = producto.getVariaciones().stream()
                .map(v -> new VariacionDto(v.getId(), v.getTalle(), v.getColor(), v.getStock(), v.getActivo()))
                .toList();

        return new ProductoResponseDto(
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getCategoria(),
                producto.getRutasImagenes(),
                producto.getActivo(),
                variacionesDto
        );
    }
}