package com.example.pacientes.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.pacientes.dto.DepartamentoRequest;
import com.example.pacientes.dto.DepartamentoResponse;
import com.example.pacientes.dto.DiagnosticoRequest;
import com.example.pacientes.dto.DiagnosticoResponse;
import com.example.pacientes.dto.MunicipioRequest;
import com.example.pacientes.dto.MunicipioResponse;
import com.example.pacientes.service.CatalogoService;

@RestController
public class CatalogoController {
    private final CatalogoService catalogoService;

    public CatalogoController(CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
    }

    @GetMapping("/departamentos")
    public List<DepartamentoResponse> listarDepartamentos() {
        return catalogoService.listarDepartamentos();
    }

    @PostMapping("/departamentos")
    public ResponseEntity<DepartamentoResponse> crearDepartamento(@RequestBody DepartamentoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(catalogoService.crearDepartamento(request));
    }

    @GetMapping("/municipios")
    public List<MunicipioResponse> listarMunicipios(
            @RequestParam(required = false) Long departamentoId) {
        return catalogoService.listarMunicipios(departamentoId);
    }

    @PostMapping("/municipios")
    public ResponseEntity<MunicipioResponse> crearMunicipio(@RequestBody MunicipioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(catalogoService.crearMunicipio(request));
    }

    @PutMapping("/municipios/{id}/departamento/{departamentoId}")
    public MunicipioResponse asociarMunicipioDepartamento(
            @PathVariable Long id,
            @PathVariable Long departamentoId) {
        return catalogoService.asociarMunicipioDepartamento(id, departamentoId);
    }

    @GetMapping("/diagnosticos")
    public List<DiagnosticoResponse> listarDiagnosticos() {
        return catalogoService.listarDiagnosticos();
    }

    @PostMapping("/diagnosticos")
    public ResponseEntity<DiagnosticoResponse> crearDiagnostico(@RequestBody DiagnosticoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(catalogoService.crearDiagnostico(request));
    }
}
