package com.example.pacientes.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.pacientes.model.Paciente;
import com.example.pacientes.service.PacienteService;



@RestController
@RequestMapping("/pacientes")
public class PacienteController {

    private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }


    @PostMapping
    public ResponseEntity<String> crearPaciente(@RequestBody Paciente paciente) {
        Paciente pacienteCreado = pacienteService.savePaciente(paciente);
        return ResponseEntity.status(HttpStatus.CREATED).body(pacienteCreado.getNombre() + " " + pacienteCreado.getApellido() + " creado con éxito");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Paciente> obtenerPaciente(@PathVariable Long id) {
        Paciente paciente = pacienteService.getPacienteById(id);
        return ResponseEntity.ok(paciente);
    }


    @GetMapping
    public ResponseEntity<List<Paciente>> getAllPacientes() {
        return ResponseEntity.ok(pacienteService.getAllPacientes());
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizarPaciente(@PathVariable Long id, @RequestBody Paciente paciente) {
        Paciente pacienteActualizado = pacienteService.updatePaciente(id, paciente);
        return ResponseEntity.ok(pacienteActualizado.getNombre() + " " + pacienteActualizado.getApellido() + " actualizado con éxito");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPaciente(@PathVariable Long id) {
        return pacienteService.deletePaciente(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/diagnosticar")
    public ResponseEntity<String> diagnosticar(
            @PathVariable Long id,
            @RequestParam String nombre,
            @RequestParam(required = false) String descripcion) {

        Paciente paciente = pacienteService.otorgarDiagnostico(id, nombre, descripcion);
        return ResponseEntity.ok("Diagnóstico " + paciente.getDiagnostico().getCodigo_diagnostico() + " procesado correctamente.");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> manejarErrores(IllegalArgumentException ex) {
        HttpStatus status = ex.getMessage() != null && ex.getMessage().contains("no encontrado")
                ? HttpStatus.NOT_FOUND
                : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(ex.getMessage());
    }

}
