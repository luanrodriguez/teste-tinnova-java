package com.example.teste_tinnova_java.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateVehicleDTO(
        @NotBlank
        String marca,
        @NotBlank
        String placa,
        @NotBlank
        String cor,
        @NotNull
        Integer ano,
        @NotNull
        Double preco
) {
}
