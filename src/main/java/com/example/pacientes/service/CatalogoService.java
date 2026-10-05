package com.example.pacientes.service;

import java.util.List;

import com.example.pacientes.dto.DepartamentoRequest;
import com.example.pacientes.dto.DepartamentoResponse;
import com.example.pacientes.dto.DiagnosticoRequest;
import com.example.pacientes.dto.DiagnosticoResponse;
import com.example.pacientes.dto.MunicipioRequest;
import com.example.pacientes.dto.MunicipioResponse;

public interface CatalogoService {
    List<DepartamentoResponse> listarDepartamentos();

    DepartamentoResponse crearDepartamento(DepartamentoRequest request);

    List<MunicipioResponse> listarMunicipios(Long departamentoId);

    MunicipioResponse crearMunicipio(MunicipioRequest request);

    MunicipioResponse asociarMunicipioDepartamento(Long municipioId, Long departamentoId);

    List<DiagnosticoResponse> listarDiagnosticos();

    DiagnosticoResponse crearDiagnostico(DiagnosticoRequest request);
}
