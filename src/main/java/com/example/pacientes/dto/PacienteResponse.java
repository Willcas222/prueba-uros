package com.example.pacientes.dto;

import java.time.LocalDate;

import com.example.pacientes.model.Paciente;

public record PacienteResponse(
        Long id,
        String nombre,
        String apellido,
        LocalDate fechaNacimiento,
        Long municipioId,
        String municipio,
        Long departamentoId,
        String departamento,
        String codigoDiagnostico,
        String nombreDiagnostico,
        String observacionMedica) {

    public static PacienteResponse from(Paciente paciente) {
        var municipio = paciente.getMunicipio();
        var departamento = municipio == null ? null : municipio.getDepartamento();
        var diagnostico = paciente.getDiagnostico();
        return new PacienteResponse(
                paciente.getId(),
                paciente.getNombre(),
                paciente.getApellido(),
                paciente.getFechaNacimiento(),
                municipio == null ? null : municipio.getId(),
                municipio == null ? null : municipio.getNombre(),
                departamento == null ? null : departamento.getId(),
                departamento == null ? null : departamento.getNombre(),
                diagnostico == null ? null : diagnostico.getCodigo(),
                diagnostico == null ? null : diagnostico.getNombre(),
                paciente.getObservacionMedica());
    }
}
