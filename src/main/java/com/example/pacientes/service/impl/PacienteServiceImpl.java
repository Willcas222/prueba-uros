package com.example.pacientes.service.impl;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.pacientes.dto.AsignacionDiagnosticoRequest;
import com.example.pacientes.dto.PacienteRequest;
import com.example.pacientes.dto.PacienteResponse;
import com.example.pacientes.exception.RecursoNoEncontradoException;
import com.example.pacientes.model.Paciente;
import com.example.pacientes.repository.DiagnosticoRepository;
import com.example.pacientes.repository.MunicipioRepository;
import com.example.pacientes.repository.PacienteRepository;
import com.example.pacientes.service.PacienteService;

@Service
public class PacienteServiceImpl implements PacienteService {
    private final PacienteRepository pacienteRepository;
    private final MunicipioRepository municipioRepository;
    private final DiagnosticoRepository diagnosticoRepository;

    public PacienteServiceImpl(
            PacienteRepository pacienteRepository,
            MunicipioRepository municipioRepository,
            DiagnosticoRepository diagnosticoRepository) {
        this.pacienteRepository = pacienteRepository;
        this.municipioRepository = municipioRepository;
        this.diagnosticoRepository = diagnosticoRepository;
    }

    @Override
    @Transactional
    public PacienteResponse crear(PacienteRequest request) {
        validarPaciente(request);
        var municipio = municipioRepository.findById(request.municipioId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Municipio no encontrado"));
        if (municipio.getDepartamento() == null) {
            throw new IllegalArgumentException("El municipio debe estar asociado a un departamento");
        }
        var paciente = new Paciente(
                request.nombre().trim(),
                request.apellido().trim(),
                request.fechaNacimiento(),
                municipio);
        paciente.setEdad(Period.between(request.fechaNacimiento(), LocalDate.now()).getYears());
        return respuesta(pacienteRepository.save(paciente));
    }

    @Override
    @Transactional
    public PacienteResponse actualizar(Long id, PacienteRequest request) {
        validarPaciente(request);
        var paciente = obtenerEntidad(id);
        var municipio = municipioRepository.findById(request.municipioId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Municipio no encontrado"));
        if (municipio.getDepartamento() == null) {
            throw new IllegalArgumentException("El municipio debe estar asociado a un departamento");
        }
        paciente.setNombre(request.nombre().trim());
        paciente.setApellido(request.apellido().trim());
        paciente.setFechaNacimiento(request.fechaNacimiento());
        paciente.setEdad(Period.between(request.fechaNacimiento(), LocalDate.now()).getYears());
        paciente.setMunicipio(municipio);
        return respuesta(pacienteRepository.save(paciente));
    }

    @Override
    @Transactional(readOnly = true)
    public PacienteResponse obtener(Long id) {
        return respuesta(obtenerEntidad(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PacienteResponse> listar() {
        return pacienteRepository.findAll().stream().map(PacienteResponse::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PacienteResponse> listarDiagnosticados() {
        return pacienteRepository.findAllByDiagnosticoIsNotNull()
                .stream().map(PacienteResponse::from).toList();
    }

    @Override
    @Transactional
    public PacienteResponse asignarDiagnostico(Long pacienteId, AsignacionDiagnosticoRequest request) {
        if (request == null || request.codigoDiagnostico() == null || request.codigoDiagnostico().isBlank()) {
            throw new IllegalArgumentException("El código del diagnóstico es obligatorio");
        }
        if (request.observacion() == null || request.observacion().isBlank()) {
            throw new IllegalArgumentException("La observación médica es obligatoria");
        }
        if (request.observacion().length() > 2000) {
            throw new IllegalArgumentException("La observación médica no puede superar 2000 caracteres");
        }
        var paciente = obtenerEntidad(pacienteId);
        var diagnostico = diagnosticoRepository.findByCodigoIgnoreCase(request.codigoDiagnostico().trim())
                .orElseThrow(() -> new RecursoNoEncontradoException("Diagnóstico no encontrado"));
        paciente.setDiagnostico(diagnostico);
        paciente.setObservacionMedica(request.observacion().trim());
        return respuesta(pacienteRepository.save(paciente));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        pacienteRepository.delete(obtenerEntidad(id));
    }

    private Paciente obtenerEntidad(Long id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Paciente no encontrado"));
    }

    private void validarPaciente(PacienteRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Los datos del paciente son obligatorios");
        }
        if (request.nombre() == null || request.nombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del paciente es obligatorio");
        }
        if (request.apellido() == null || request.apellido().isBlank()) {
            throw new IllegalArgumentException("El apellido del paciente es obligatorio");
        }
        if (request.fechaNacimiento() == null || request.fechaNacimiento().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de nacimiento es obligatoria y no puede ser futura");
        }
        if (request.municipioId() == null) {
            throw new IllegalArgumentException("El municipio del paciente es obligatorio");
        }
    }

    private PacienteResponse respuesta(Paciente paciente) {
        return PacienteResponse.from(paciente);
    }
}
