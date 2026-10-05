package com.example.pacientes.dto;

import com.example.pacientes.model.Departamento;

public record DepartamentoResponse(Long id, String nombre) {
    public static DepartamentoResponse from(Departamento departamento) {
        return new DepartamentoResponse(departamento.getId(), departamento.getNombre());
    }
}
