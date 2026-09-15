package Amma.e_comerce.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import Amma.e_comerce.model.Administrador;

@Repository
public interface AdministradorRepository extends JpaRepository<Administrador, Long> {
    // Spring Boot crea la consulta "SELECT * FROM administrador WHERE email = ?"
    Optional<Administrador> findByEmail(String email);
}