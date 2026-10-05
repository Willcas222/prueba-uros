package com.example.pacientes.dto;

import com.example.pacientes.model.Municipio;

public record MunicipioResponse(Long id, String nombre, Long departamentoId, String departamento) {
    public static MunicipioResponse from(Municipio municipio) {
        var departamento = municipio.getDepartamento();
        return new MunicipioResponse(
                municipio.getId(),
                municipio.getNombre(),
                departamento == null ? null : departamento.getId(),
                departamento == null ? null : departamento.getNombre());
    }
}
