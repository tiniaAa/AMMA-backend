package Amma.e_comerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import Amma.e_comerce.model.Administrador;

@Repository
public interface AdministradorRepository extends JpaRepository<Administrador, Long> {

}
