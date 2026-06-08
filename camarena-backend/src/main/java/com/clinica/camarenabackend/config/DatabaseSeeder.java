package com.clinica.camarenabackend.config;

import com.clinica.camarenabackend.models.entities.Rol;
import com.clinica.camarenabackend.models.entities.Usuario;
import com.clinica.camarenabackend.repositories.RolRepository;
import com.clinica.camarenabackend.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        seedRoles();
        seedSuperAdmin();
    }

    private void seedRoles() {
        List<String> roles = Arrays.asList("ROLE_ADMIN", "ROLE_RECEPCION", "ROLE_BIOLOGO", "ROLE_PACIENTE");

        for (String nombreRol : roles) {
            if (!rolRepository.existsByOnombre_rol(nombreRol)) {
                Rol rol = Rol.builder().onombre_rol(nombreRol).build();
                rolRepository.save(rol);
                System.out.println("Rol creado: " + nombreRol);
            }
        }
    }

    private void seedSuperAdmin() {
        String adminEmail = "admin@camarena.com";

        if (!usuarioRepository.existsByEmail(adminEmail)) {
            // Asumimos que seedRoles ya se ejecutó y ROLE_ADMIN existe
            Rol adminRol = rolRepository.findByOnombre_rol("ROLE_ADMIN")
                    .orElseThrow(() -> new RuntimeException("Error: No se encontró el rol ROLE_ADMIN."));

            Usuario adminUser = Usuario.builder()
                    .email(adminEmail)
                    .opassword_hash(passwordEncoder.encode("admin123")) // Contraseña por defecto encriptada
                    .oactivo(true)
                    .rol(adminRol)
                    .build();
            usuarioRepository.save(adminUser);
            System.out.println("Super Admin creado exitosamente con el email: " + adminEmail);
        }
    }
}