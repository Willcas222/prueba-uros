package com.example.pacientes.dto;

import java.time.LocalDate;

public record PacienteRequest(String nombre, String apellido, LocalDate fechaNacimiento, Long municipioId) {
}
