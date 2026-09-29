package Amma.e_comerce.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import Amma.e_comerce.model.Orden;
@Repository
public interface OrdenRepository extends JpaRepository<Orden, Long> {
    // Spring genera el SQL automáticamente al leer "By", "And", y "Before"
    List<Orden> findByMetodoPagoAndEstadoPagoAndFechaCompraBefore(String metodoPago, String estadoPago, LocalDateTime limite);
}