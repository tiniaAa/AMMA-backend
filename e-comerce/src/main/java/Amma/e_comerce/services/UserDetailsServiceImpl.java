package Amma.e_comerce.services;

import Amma.e_comerce.model.Administrador;
import Amma.e_comerce.repository.AdministradorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AdministradorRepository administradorRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Administrador admin = administradorRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Administrador no encontrado: " + email));

        // Le decimos a Spring Security cómo se llama el usuario, su clave encriptada y sus permisos
        return new org.springframework.security.core.userdetails.User(
                admin.getEmail(),
                admin.getPasswordHash(),
                Collections.emptyList() // Aquí irían los "Roles", por ahora lo dejamos vacío
        );
    }
}