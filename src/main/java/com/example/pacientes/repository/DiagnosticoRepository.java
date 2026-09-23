package com.example.pacientes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.pacientes.model.diagnostico;

@Repository
public interface DiagnosticoRepository extends JpaRepository<diagnostico, Long> {

}
