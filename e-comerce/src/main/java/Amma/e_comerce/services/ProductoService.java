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
        // El campo 'activo' ya nace en 'true' por defecto gracias a la Entidad
        
        Producto guardado = productoRepository.save(producto);
        return mapearResponseDto(guardado);
    }

    // --- MÉTODOS PARA EL CATÁLOGO PÚBLICO (Solo activos) ---
    
    public List<ProductoResponseDto> ObtenerTodos(){
        // Cambiamos findAll() por nuestro nuevo método
        return productoRepository.findByActivoTrue().stream()
                .map(this::mapearResponseDto)
                .toList();
    }

    public List<ProductoResponseDto> obtenerPorCategoria(String categoria) {
        // Cambiamos findByCategoria por el que también filtra los activos
    	return productoRepository.findByCategoriaIgnoreCaseAndActivoTrue(categoria).stream()
                .map(this::mapearResponseDto)
                .toList();
    }

    // --- MÉTODO EXCLUSIVO PARA EL ADMIN (Todos, activos e inactivos) ---
    
    public List<ProductoResponseDto> obtenerTodosAdmin(){
        return productoRepository.findAll().stream()
                .map(this::mapearResponseDto)
                .toList();
    }

    // --- CRUD ESTÁNDAR ---

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
        producto.setRutaImagen(request.rutaImagen());
        producto.setStock(request.stock());
        
        Producto actualizado = productoRepository.save(producto);
        return mapearResponseDto(actualizado);
    }

    // --- LA MAGIA DE LA BAJA LÓGICA ---

    public void eliminar(Long id) {
        // 1. Buscamos el producto en lugar de usar deleteById()
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("El producto no existe en la base de datos"));
        
        // 2. Le cambiamos el estado a false (Inactivo)
        producto.setActivo(false);
        
        // 3. Guardamos los cambios
        productoRepository.save(producto);
    }
 // --- MÉTODO PARA RESTAURAR (REVIVIR) UN PRODUCTO ---
    public void restaurar(Long id) {
        // 1. Buscamos el producto
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("El producto no existe en la base de datos"));
        
        // 2. Le cambiamos el estado a true (Activo)
        producto.setActivo(true);
        
        // 3. ¡Guardamos los cambios en la base de datos!
        productoRepository.save(producto);
    }
    
    // --- MAPEO ---
    
    public ProductoResponseDto mapearResponseDto (Producto producto) {
        return new ProductoResponseDto(
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getStock(),
                producto.getCategoria(),
                producto.getRutaImagen(),
                producto.getActivo()
        );
    }
}