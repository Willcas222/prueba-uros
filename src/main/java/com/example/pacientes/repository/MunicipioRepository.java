package com.example.pacientes.repository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.pacientes.model.Municipio;

@Repository
public interface MunicipioRepository extends JpaRepository<Municipio, Long> {
    Optional<Municipio> findByNombreIgnoreCaseAndDepartamentoId(String nombre, Long departamentoId);

    @EntityGraph(attributePaths = "departamento")
    List<Municipio> findAllByOrderByNombreAsc();

    @EntityGraph(attributePaths = "departamento")
    List<Municipio> findAllByDepartamentoIdOrderByNombreAsc(Long departamentoId);
}
