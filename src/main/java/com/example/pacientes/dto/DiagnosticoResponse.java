package com.example.pacientes.dto;

import com.example.pacientes.model.Diagnostico;

public record DiagnosticoResponse(Long id, String codigo, String nombre, String descripcion) {
    public static DiagnosticoResponse from(Diagnostico diagnostico) {
        return new DiagnosticoResponse(
                diagnostico.getId(),
                diagnostico.getCodigo(),
                diagnostico.getNombre(),
                diagnostico.getDescripcion());
    }
}
