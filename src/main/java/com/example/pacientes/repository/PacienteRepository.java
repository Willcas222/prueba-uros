package com.example.pacientes.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.pacientes.model.Paciente;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    @EntityGraph(attributePaths = { "municipio", "municipio.departamento", "diagnostico" })
    List<Paciente> findAllByDiagnosticoIsNotNull();

    @Override
    @EntityGraph(attributePaths = { "municipio", "municipio.departamento", "diagnostico" })
    List<Paciente> findAll();

    @Override
    @EntityGraph(attributePaths = { "municipio", "municipio.departamento", "diagnostico" })
    java.util.Optional<Paciente> findById(Long id);
}
