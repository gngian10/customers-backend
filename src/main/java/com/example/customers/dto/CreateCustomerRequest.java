package com.example.customers.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

public record CreateCustomerRequest(
        @NotBlank String nombre,
        @NotBlank String apellido,
        @NotBlank @Email String email,
        @NotBlank String dni,
        @NotNull @PastOrPresent LocalDate fechaNacimiento
) {
}
