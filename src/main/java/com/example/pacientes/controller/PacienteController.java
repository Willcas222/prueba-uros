package com.example.pacientes.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pacientes.dto.AsignacionDiagnosticoRequest;
import com.example.pacientes.dto.PacienteRequest;
import com.example.pacientes.dto.PacienteResponse;
import com.example.pacientes.service.PacienteService;

@RestController
@RequestMapping("/pacientes")
public class PacienteController {
    private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @PostMapping
    public ResponseEntity<PacienteResponse> crear(@RequestBody PacienteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pacienteService.crear(request));
    }

    @GetMapping("/{id}")
    public PacienteResponse obtener(@PathVariable Long id) {
        return pacienteService.obtener(id);
    }

    @GetMapping
    public List<PacienteResponse> listar() {
        return pacienteService.listar();
    }

    @GetMapping("/diagnosticados")
    public List<PacienteResponse> listarDiagnosticados() {
        return pacienteService.listarDiagnosticados();
    }

    @PutMapping("/{id}")
    public PacienteResponse actualizar(@PathVariable Long id, @RequestBody PacienteRequest request) {
        return pacienteService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        pacienteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/diagnostico")
    public PacienteResponse asignarDiagnostico(
            @PathVariable Long id,
            @RequestBody AsignacionDiagnosticoRequest request) {
        return pacienteService.asignarDiagnostico(id, request);
    }
}
