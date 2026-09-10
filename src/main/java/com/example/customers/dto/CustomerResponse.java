package com.example.customers.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record CustomerResponse(
        Long id,
        String nombre,
        String apellido,
        String email,
        String dni,
        LocalDateTime fechaCreacion,
        LocalDate fechaNacimiento
) {
}
