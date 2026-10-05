package com.example.pacientes.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pacientes.model.Departamento;

public interface DepartamentoRepository extends JpaRepository<Departamento, Long> {
    Optional<Departamento> findByNombreIgnoreCase(String nombre);

    List<Departamento> findAllByOrderByNombreAsc();
}
