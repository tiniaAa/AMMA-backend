package Amma.e_comerce.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import Amma.e_comerce.dto.ProductoRequestDto;
import Amma.e_comerce.dto.ProductoResponseDto;
import Amma.e_comerce.services.ProductoService;
import lombok.RequiredArgsConstructor;

@RestController 
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {
    
    private final ProductoService productoService;
    
    // GET: Busca todos o filtra por categoría (/api/productos o /api/productos?categoria=Pack)
    @GetMapping
    public ResponseEntity<List<ProductoResponseDto>> obtenerProductos(
            @RequestParam(required = false) String categoria) {
        
        if (categoria != null && !categoria.trim().isEmpty()) {
            return ResponseEntity.ok(productoService.obtenerPorCategoria(categoria));
        }
        return ResponseEntity.ok(productoService.ObtenerTodos());
    }

    // GET: Busca un producto puntual (/api/productos/1)
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.obtenerPorId(id));
    }

    // POST: Crea un producto nuevo
    @PostMapping
    public ResponseEntity<ProductoResponseDto> crearProducto(@RequestBody ProductoRequestDto request){
        ProductoResponseDto nuevo = productoService.crear(request);
        return new ResponseEntity<>(nuevo, HttpStatus.CREATED);
    }

    // PUT: Actualiza un producto existente
    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponseDto> actualizarProducto(
            @PathVariable Long id, 
            @RequestBody ProductoRequestDto request) {
        ProductoResponseDto actualizado = productoService.actualizar(id, request);
        return ResponseEntity.ok(actualizado);
    }

    // DELETE: Elimina un producto
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        productoService.eliminar(id);
        // Retornamos 204 No Content, que es el estándar para un borrado exitoso
        return ResponseEntity.noContent().build(); 
    }
 // GET EXCLUSIVO ADMIN: Trae absolutamente todos los registros (Activos e Inactivos)
    @GetMapping("/admin/todos")
    public ResponseEntity<List<ProductoResponseDto>> obtenerTodosAdmin() {
        return ResponseEntity.ok(productoService.obtenerTodosAdmin());
    }
 // PUT: Restaura un producto inactivo
    @PutMapping("/{id}/restaurar")
    public ResponseEntity<Void> restaurarProducto(@PathVariable Long id) {
        productoService.restaurar(id);
        return ResponseEntity.noContent().build();
    }
}