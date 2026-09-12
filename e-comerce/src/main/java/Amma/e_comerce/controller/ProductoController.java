package Amma.e_comerce.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Amma.e_comerce.dto.ProductoRequestDto;
import Amma.e_comerce.dto.ProductoResponseDto;
import Amma.e_comerce.services.ProductoService;
import lombok.RequiredArgsConstructor;

@RestController 
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173") 
public class ProductoController {
	
	private final ProductoService productoService;
	
	@GetMapping("/todos")
	public ResponseEntity <List<ProductoResponseDto>> obtenerTodos (){
		return ResponseEntity.ok(productoService.ObtenerTodos());
	}
	@PostMapping("/crear")
	public ResponseEntity <ProductoResponseDto> crearProducto(@RequestBody ProductoRequestDto request){
		ProductoResponseDto nuevo = productoService.crear(request);
		return new ResponseEntity<>(nuevo, HttpStatus.CREATED);
	}
	
}
