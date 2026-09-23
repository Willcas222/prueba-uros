package com.example.pacientes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.pacientes.model.Paciente;

@Repository 
public interface Pacienterepository extends JpaRepository<Paciente, Long> {
    
}
