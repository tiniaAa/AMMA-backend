package Amma.e_comerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import Amma.e_comerce.model.Orden;
@Repository
public interface OrdenRepository extends JpaRepository<Orden, Long> {

}
