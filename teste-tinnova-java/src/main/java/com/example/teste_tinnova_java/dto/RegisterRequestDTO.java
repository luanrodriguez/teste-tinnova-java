package com.example.teste_tinnova_java.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterRequestDTO(@NotBlank String email, @NotBlank String password, String role) {
}
