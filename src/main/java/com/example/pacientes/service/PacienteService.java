package com.example.pacientes.service;

import java.util.List;

import com.example.pacientes.dto.AsignacionDiagnosticoRequest;
import com.example.pacientes.dto.PacienteRequest;
import com.example.pacientes.dto.PacienteResponse;

public interface PacienteService {
    PacienteResponse crear(PacienteRequest request);

    PacienteResponse actualizar(Long id, PacienteRequest request);

    PacienteResponse obtener(Long id);

    List<PacienteResponse> listar();

    List<PacienteResponse> listarDiagnosticados();

    PacienteResponse asignarDiagnostico(Long pacienteId, AsignacionDiagnosticoRequest request);

    void eliminar(Long id);
}
