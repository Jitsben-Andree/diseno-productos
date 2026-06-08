package com.clinica.camarenabackend.services.impl;

import com.clinica.camarenabackend.dtos.request.EmpleadoRequest;
import com.clinica.camarenabackend.dtos.response.EmpleadoResponse;
import com.clinica.camarenabackend.models.entities.Empleado;
import com.clinica.camarenabackend.models.entities.Rol;
import com.clinica.camarenabackend.models.entities.Usuario;
import com.clinica.camarenabackend.repositories.EmpleadoRepository;
import com.clinica.camarenabackend.repositories.RolRepository;
import com.clinica.camarenabackend.repositories.UsuarioRepository;
import com.clinica.camarenabackend.services.interfaces.EmpleadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmpleadoServiceImpl implements EmpleadoService {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public EmpleadoResponse registrarEmpleado(EmpleadoRequest request) {

        // 1. Validaciones de negocio (Evitar duplicados)
        if (empleadoRepository.existsByOdni(request.getDni())) {
            throw new RuntimeException("Error: Ya existe un empleado registrado con este DNI.");
        }
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Error: El email ya está en uso en el sistema.");
        }

        // 2. Buscar el Rol en la BD
        Rol rolAsignado = rolRepository.findByOnombre_rol(request.getNombreRol().toUpperCase())
                .orElseThrow(() -> new RuntimeException("Error: El rol especificado no existe."));

        // 3. Crear el Usuario (Credenciales)
        // Por defecto, la contraseña será el DNI del empleado (encriptado, por supuesto)
        Usuario nuevoUsuario = Usuario.builder()
                .email(request.getEmail())
                .opassword_hash(passwordEncoder.encode(request.getDni()))
                .oactivo(true)
                .rol(rolAsignado)
                .build();

        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);

        // 4. Crear el perfil del Empleado y vincularlo al Usuario
        Empleado nuevoEmpleado = Empleado.builder()
                .odni(request.getDni())
                .onombres(request.getNombres())
                .oapellidos(request.getApellidos())
                .cmpColegiatura(request.getCmpColegiatura())
                .cargo(request.getCargo())
                .usuario(usuarioGuardado)
                .build();

        Empleado empleadoGuardado = empleadoRepository.save(nuevoEmpleado);

        // 5. Retornar el Response
        return mapearAResponse(empleadoGuardado);
    }

    @Override
    public List<EmpleadoResponse> listarEmpleados() {
        return empleadoRepository.findAll().stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    // Método auxiliar para no repetir código
    private EmpleadoResponse mapearAResponse(Empleado empleado) {
        return EmpleadoResponse.builder()
                .idEmpleado(empleado.getOid_empleado())
                .dni(empleado.getOdni())
                .nombres(empleado.getOnombres())
                .apellidos(empleado.getOapellidos())
                .cmpColegiatura(empleado.getCmpColegiatura())
                .cargo(empleado.getCargo())
                .email(empleado.getUsuario().getEmail())
                .rolAsignado(empleado.getUsuario().getRol().getOnombre_rol())
                .activo(empleado.getUsuario().getOactivo())
                .build();
    }
}
