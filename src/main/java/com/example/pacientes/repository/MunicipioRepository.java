package com.example.pacientes.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.pacientes.model.Municipio;

@Repository
public interface MunicipioRepository extends JpaRepository<Municipio, Long> {
    Optional<Municipio> findByNombreIgnoreCase(String nombre);
}
