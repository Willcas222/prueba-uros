package com.example.pacientes.service.impl;

import com.example.pacientes.model.Municipio;
import com.example.pacientes.model.Paciente;
import com.example.pacientes.model.diagnostico;
import com.example.pacientes.repository.DiagnosticoRepository;
import com.example.pacientes.repository.MunicipioRepository;
import com.example.pacientes.repository.Pacienterepository;
import com.example.pacientes.service.PacienteService;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
public class PacienteServiceImpl implements PacienteService {

    private final Pacienterepository pacienteRepository;
    private final MunicipioRepository municipioRepository;
    private final DiagnosticoRepository diagnosticoRepository;

    public PacienteServiceImpl(Pacienterepository pacienteRepository, MunicipioRepository municipioRepository,
            DiagnosticoRepository diagnosticoRepository) {
        this.pacienteRepository = pacienteRepository;
        this.municipioRepository = municipioRepository;
        this.diagnosticoRepository = diagnosticoRepository;
    }

    @Override
    @Transactional
    public Paciente savePaciente(Paciente paciente) {
        validatePaciente(paciente);
        
        Municipio municipioInput = paciente.getMunicipio();
        
        
        Municipio municipioResuelto = resolverMunicipio(municipioInput);
        
        
        paciente.setMunicipio(municipioResuelto);
        
        return pacienteRepository.save(paciente);
    }

    @Override
    @Transactional
    public Paciente updatePaciente(Long id, Paciente paciente) {
        validatePaciente(paciente);
        Paciente updatedPaciente = getPacienteById(id);
        updatedPaciente.setNombre(paciente.getNombre());
        updatedPaciente.setApellido(paciente.getApellido());
        updatedPaciente.setEdad(paciente.getEdad());
        updatedPaciente.setFechaNacimiento(paciente.getFechaNacimiento());
        updatedPaciente.setMunicipio(resolverMunicipio(paciente.getMunicipio()));
        return pacienteRepository.save(updatedPaciente);
    }

    @Override
    @Transactional(readOnly = true)
    public Paciente getPacienteById(Long id) {
        return pacienteRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado"));
    }

    @Override
    @Transactional
    public Boolean deletePaciente(Long id) {
        return pacienteRepository.findById(id).map(paciente -> {
            pacienteRepository.delete(paciente);
            return true;
        }).orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public Paciente getPacienteByDiagnosticoId(Long diagnosticoId) {
        return pacienteRepository.findAll().stream()
                .filter(paciente -> paciente.getDiagnostico() != null && diagnosticoId != null
                        && diagnosticoId.equals(paciente.getDiagnostico().getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Paciente con diagnostico ID " + diagnosticoId + " no encontrado"));
    }

    @Override
    @Transactional
    public Paciente otorgarDiagnostico(Long pacienteId, String nombre, String descripcion) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del diagnóstico no puede estar vacío");
        }
        Paciente paciente = getPacienteById(pacienteId);
        diagnostico nuevoDiagnostico = diagnosticoRepository.save(new diagnostico(nombre, descripcion));
        nuevoDiagnostico.setCodigo_diagnostico(String.format("N%04d", nuevoDiagnostico.getId()));
        paciente.setDiagnostico(nuevoDiagnostico);
        return pacienteRepository.save(paciente);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Paciente> getAllPacientes() {
        return pacienteRepository.findAll();
    }


    private void validatePaciente(Paciente paciente) {
        if (paciente == null) {
            throw new IllegalArgumentException("El paciente no puede ser nulo");
        }
        if (paciente.getNombre() == null || paciente.getNombre().isEmpty()) {
            throw new IllegalArgumentException("El nombre del paciente no puede estar vacío");
        }
        if (paciente.getApellido() == null || paciente.getApellido().isEmpty()) {
            throw new IllegalArgumentException("El apellido del paciente no puede estar vacío");
        }
        if (paciente.getEdad() < 0) {
            throw new IllegalArgumentException("La edad del paciente no puede ser negativa");
        }
        if (paciente.getFechaNacimiento() == null) {
            throw new IllegalArgumentException("La fecha de nacimiento del paciente no puede ser nula");
        }
        if (paciente.getMunicipio() == null || paciente.getMunicipio().getNombre() == null
                || paciente.getMunicipio().getNombre().isBlank()) {
            throw new IllegalArgumentException("El municipio del paciente no puede estar vacío");
        }
    }

    
    private Municipio resolverMunicipio(Municipio municipio) {
        String nombre = municipio.getNombre().trim();
        return municipioRepository.findByNombreIgnoreCase(nombre)
                .orElseGet(() -> municipioRepository.save(new Municipio(nombre)));
    }
}
