package Amma.e_comerce.repository;



import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import Amma.e_comerce.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    
	// Para el catálogo general: trae solo los que no están eliminados
    List<Producto> findByActivoTrue();

    // NUEVO: Filtra por categoría ignorando mayúsculas/minúsculas y que estén activos
    List<Producto> findByCategoriaIgnoreCaseAndActivoTrue(String categoria);
}