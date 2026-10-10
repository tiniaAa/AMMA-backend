package Amma.e_comerce.Config;

import Amma.e_comerce.model.Administrador;
import Amma.e_comerce.repository.AdministradorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private final AdministradorRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Value("${ADMIN_EMAIL}")
    private String adminEmail;

    @Value("${ADMIN_PASSWORD}")
    private String adminPassword;

    @Override
    public void run(String... args) throws Exception {
        if (repository.count() == 0) {
            Administrador admin = new Administrador();
            admin.setEmail(adminEmail);
            admin.setPasswordHash(passwordEncoder.encode(adminPassword)); 
            
            repository.save(admin);
            System.out.println("✅ Administrador creado de forma segura desde las variables de entorno.");
        }
    }
}