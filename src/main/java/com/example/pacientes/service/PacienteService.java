package com.example.pacientes.service;

import java.util.List;

import com.example.pacientes.model.Paciente;

public interface PacienteService {
    Paciente savePaciente(Paciente paciente);
    Paciente updatePaciente(Long id, Paciente paciente);
    Paciente getPacienteById(Long id);
    List<Paciente> getAllPacientes();
    Boolean deletePaciente(Long id);
    Paciente getPacienteByDiagnosticoId(Long diagnosticoId);
    Paciente otorgarDiagnostico(Long pacienteId, String nombre, String descripcion);

}
