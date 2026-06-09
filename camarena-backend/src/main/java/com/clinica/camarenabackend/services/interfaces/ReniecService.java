package com.clinica.camarenabackend.services.interfaces;

import com.clinica.camarenabackend.dtos.response.ReniecResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ReniecService {

    @Value("${api.reniec.url}")
    private String apiUrl;

    @Value("${api.reniec.token}")
    private String token;

    private final RestClient restClient;

    public ReniecService() {
        this.restClient = RestClient.create();
    }

    public ReniecResponseDto consultarDni(String dni) {
        try {
            // Construimos la URL exacta: https://dniruc.apisperu.com/api/v1/dni/{numero}?token={token}
            String urlCompleta = apiUrl + "/" + dni + "?token=" + token;

            ReniecResponseDto response = restClient.get()
                    .uri(urlCompleta)
                    .header("Accept", "application/json")
                    .retrieve()
                    .body(ReniecResponseDto.class);

            // Validación: Si la API responde HTTP 200 pero "success" es false (ej. DNI no existe)
            if (response == null || !response.isSuccess()) {
                throw new RuntimeException("El número de DNI no se encuentra registrado.");
            }

            return response;

        } catch (Exception e) {
            throw new RuntimeException("Error al conectar con el servicio de DNI: " + e.getMessage());
        }
    }
}