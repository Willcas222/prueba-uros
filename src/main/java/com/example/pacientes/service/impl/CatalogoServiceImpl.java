package com.example.pacientes.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.pacientes.dto.DepartamentoRequest;
import com.example.pacientes.dto.DepartamentoResponse;
import com.example.pacientes.dto.DiagnosticoRequest;
import com.example.pacientes.dto.DiagnosticoResponse;
import com.example.pacientes.dto.MunicipioRequest;
import com.example.pacientes.dto.MunicipioResponse;
import com.example.pacientes.exception.RecursoNoEncontradoException;
import com.example.pacientes.model.Departamento;
import com.example.pacientes.model.Diagnostico;
import com.example.pacientes.model.Municipio;
import com.example.pacientes.repository.DepartamentoRepository;
import com.example.pacientes.repository.DiagnosticoRepository;
import com.example.pacientes.repository.MunicipioRepository;
import com.example.pacientes.service.CatalogoService;

@Service
public class CatalogoServiceImpl implements CatalogoService {
    private final DepartamentoRepository departamentoRepository;
    private final MunicipioRepository municipioRepository;
    private final DiagnosticoRepository diagnosticoRepository;

    public CatalogoServiceImpl(
            DepartamentoRepository departamentoRepository,
            MunicipioRepository municipioRepository,
            DiagnosticoRepository diagnosticoRepository) {
        this.departamentoRepository = departamentoRepository;
        this.municipioRepository = municipioRepository;
        this.diagnosticoRepository = diagnosticoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartamentoResponse> listarDepartamentos() {
        return departamentoRepository.findAllByOrderByNombreAsc()
                .stream().map(DepartamentoResponse::from).toList();
    }

    @Override
    @Transactional
    public DepartamentoResponse crearDepartamento(DepartamentoRequest request) {
        var nombre = validarNombre(request == null ? null : request.nombre(), "departamento");
        if (departamentoRepository.findByNombreIgnoreCase(nombre).isPresent()) {
            throw new IllegalArgumentException("Ya existe un departamento con ese nombre");
        }
        return DepartamentoResponse.from(departamentoRepository.save(new Departamento(nombre)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MunicipioResponse> listarMunicipios(Long departamentoId) {
        var municipios = departamentoId == null
                ? municipioRepository.findAllByOrderByNombreAsc()
                : municipioRepository.findAllByDepartamentoIdOrderByNombreAsc(departamentoId);
        return municipios.stream().map(MunicipioResponse::from).toList();
    }

    @Override
    @Transactional
    public MunicipioResponse crearMunicipio(MunicipioRequest request) {
        if (request == null || request.departamentoId() == null) {
            throw new IllegalArgumentException("El departamento del municipio es obligatorio");
        }
        var nombre = validarNombre(request.nombre(), "municipio");
        var departamento = departamentoRepository.findById(request.departamentoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Departamento no encontrado"));
        if (municipioRepository.findByNombreIgnoreCaseAndDepartamentoId(nombre, departamento.getId()).isPresent()) {
            throw new IllegalArgumentException("Ese municipio ya está registrado en el departamento");
        }
        return MunicipioResponse.from(municipioRepository.save(new Municipio(nombre, departamento)));
    }

    @Override
    @Transactional
    public MunicipioResponse asociarMunicipioDepartamento(Long municipioId, Long departamentoId) {
        var municipio = municipioRepository.findById(municipioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Municipio no encontrado"));
        var departamento = departamentoRepository.findById(departamentoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Departamento no encontrado"));
        municipio.setDepartamento(departamento);
        return MunicipioResponse.from(municipioRepository.save(municipio));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiagnosticoResponse> listarDiagnosticos() {
        return diagnosticoRepository.findAll().stream().map(DiagnosticoResponse::from).toList();
    }

    @Override
    @Transactional
    public DiagnosticoResponse crearDiagnostico(DiagnosticoRequest request) {
        if (request == null || request.codigo() == null || request.codigo().isBlank()) {
            throw new IllegalArgumentException("El código interno del diagnóstico es obligatorio");
        }
        var codigo = request.codigo().trim();
        var nombre = validarNombre(request.nombre(), "diagnóstico");
        if (diagnosticoRepository.findByCodigoIgnoreCase(codigo).isPresent()) {
            throw new IllegalArgumentException("Ya existe un diagnóstico con ese código");
        }
        return DiagnosticoResponse.from(
                diagnosticoRepository.save(new Diagnostico(codigo, nombre, limpiar(request.descripcion()))));
    }

    private String validarNombre(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("El nombre de " + campo + " es obligatorio");
        }
        return valor.trim();
    }

    private String limpiar(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}
