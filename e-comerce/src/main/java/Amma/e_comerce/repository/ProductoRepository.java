package Amma.e_comerce.repository;



import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import Amma.e_comerce.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    
    // Para el catálogo general: trae solo los que no están eliminados
    List<Producto> findByActivoTrue();

    // Para el filtro del catálogo: trae los activos de una categoría específica
    List<Producto> findByCategoriaAndActivoTrue(String categoria);
}