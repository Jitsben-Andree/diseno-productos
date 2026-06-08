package com.clinica.camarenabackend.security.models;

import com.clinica.camarenabackend.models.entities.Usuario;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class CustomUserDetails implements UserDetails {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private String email;

    @JsonIgnore // Evita que la contraseña viaje en la red por accidente
    private String password;

    private Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(UUID id, String email, String password,
                             Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    // Método de fábrica para convertir la Entidad Usuario a CustomUserDetails
    public static CustomUserDetails build(Usuario usuario) {
        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority(usuario.getRol().getOnombre_rol())
        );

        return new CustomUserDetails(
                usuario.getOid_usuario(),
                usuario.getEmail(),
                usuario.getOpassword_hash(),
                authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    public UUID getId() { return id; }
    @Override
    public String getPassword() { return password; }
    @Override
    public String getUsername() { return email; }
    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }
}