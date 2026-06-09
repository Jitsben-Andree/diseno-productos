package com.clinica.camarenabackend.security.models;

import com.clinica.camarenabackend.models.entities.Paciente;
import com.clinica.camarenabackend.models.entities.Usuario;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class CustomUserDetails implements UserDetails {

    // 1. DECLARA LOS ATRIBUTOS AQUÍ (Esto quitará los errores rojos)
    private UUID id;
    private String email;
    @JsonIgnore
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    // 2. CONSTRUCTOR (Asegúrate de tenerlo así)
    public CustomUserDetails(UUID id, String email, String password,
                             Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    // 3. MÉTODO BUILD PARA USUARIOS
    public static CustomUserDetails build(Usuario usuario) {
        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority(usuario.getRol().getOnombre_rol())
        );
        return new CustomUserDetails(usuario.getOid_usuario(), usuario.getEmail(), usuario.getOpassword_hash(), authorities);
    }

    // 4. MÉTODO BUILD PARA PACIENTES (Corregido)
    public static CustomUserDetails build(Paciente paciente) {
        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_PACIENTE")
        );
        // Verifica que los getters de tu Paciente se llamen exactamente así
        return new CustomUserDetails(paciente.getOid_paciente(), paciente.getOdni(), "TICKET_DEFAULT", authorities);
    }

    // 5. MÉTODOS DE UserDetails (No olvides mantener estos)
    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override public String getPassword() { return password; }
    @Override public String getUsername() { return email; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
    public UUID getId() { return id; }
}