package com.example.customers.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record CreateCustomerRequest(
        @NotBlank(message = "El nombre es obligatorio") String nombre,
        @NotBlank(message = "El apellido es obligatorio") String apellido,
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email debe tener un formato válido") String email,
        @NotBlank(message = "El DNI es obligatorio") 
        @Pattern(
                regexp = "\\d{8}",
                message = "El DNI debe contener exactamente 8 dígitos"
        )
        String dni,
        @NotNull(message = "La fecha de nacimiento es obligatoria")
        @PastOrPresent(message = "La fecha de nacimiento no puede ser futura") LocalDate fechaNacimiento
) {
}
