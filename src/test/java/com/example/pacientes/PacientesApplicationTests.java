package com.example.pacientes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.datasource.url=jdbc:h2:mem:pacientes-test;DB_CLOSE_DELAY=-1",
                "spring.datasource.username=sa",
                "spring.datasource.password=",
                "spring.datasource.driver-class-name=org.h2.Driver",
                "spring.jpa.hibernate.ddl-auto=create-drop"
        })
class PacientesApplicationTests {
    @Autowired
    private Environment environment;

    private final HttpClient http = HttpClient.newHttpClient();

    @Test
    void flujoCompletoDePacienteYDiagnostico() throws Exception {
        var pagina = request("GET", "/", null);
        assertEquals(200, pagina.statusCode());
        assertTrue(pagina.body().contains("Pacientes diagnosticados"));

        var departamento = post("/departamentos", """
                {"nombre":"Antioquia"}
                """);
        assertEquals(201, departamento.statusCode());
        var departamentoId = id(departamento.body());

        var municipio = post("/municipios", """
                {"nombre":"Medellín","departamentoId":%s}
                """.formatted(departamentoId));
        assertEquals(201, municipio.statusCode());
        var municipioId = id(municipio.body());

        var diagnostico = post("/diagnosticos", """
                {"codigo":"N189","nombre":"Insuficiencia renal crónica","descripcion":"Diagnóstico de prueba"}
                """);
        assertEquals(201, diagnostico.statusCode());
        assertTrue(diagnostico.body().contains("\"codigo\":\"N189\""));

        var paciente = post("/pacientes", """
                {"nombre":"Ana","apellido":"Pérez","fechaNacimiento":"1990-04-12","municipioId":%s}
                """.formatted(municipioId));
        assertEquals(201, paciente.statusCode());
        var pacienteId = id(paciente.body());
        assertTrue(paciente.body().contains("\"departamento\":\"Antioquia\""));

        var asignado = post("/pacientes/" + pacienteId + "/diagnostico", """
                {"codigoDiagnostico":"N189","observacion":"Control y seguimiento"}
                """);
        assertEquals(200, asignado.statusCode());
        assertTrue(asignado.body().contains("\"nombreDiagnostico\":\"Insuficiencia renal crónica\""));
        assertTrue(asignado.body().contains("\"observacionMedica\":\"Control y seguimiento\""));

        var diagnosticados = request("GET", "/pacientes/diagnosticados", null);
        assertEquals(200, diagnosticados.statusCode());
        assertTrue(diagnosticados.body().contains("\"codigoDiagnostico\":\"N189\""));
        assertTrue(diagnosticados.body().contains("\"municipio\":\"Medellín\""));

        var actualizado = request("PUT", "/pacientes/" + pacienteId, """
                {"nombre":"Ana María","apellido":"Pérez","fechaNacimiento":"1990-04-12","municipioId":%s}
                """.formatted(municipioId));
        assertEquals(200, actualizado.statusCode());
        assertTrue(actualizado.body().contains("\"codigoDiagnostico\":\"N189\""));
        assertTrue(actualizado.body().contains("\"observacionMedica\":\"Control y seguimiento\""));

        assertEquals(204, request("DELETE", "/pacientes/" + pacienteId, null).statusCode());
        assertEquals(404, request("GET", "/pacientes/" + pacienteId, null).statusCode());
    }

    private HttpResponse<String> post(String path, String body) throws Exception {
        return request("POST", path, body);
    }

    private HttpResponse<String> request(String method, String path, String body) throws Exception {
        var port = environment.getRequiredProperty("local.server.port");
        var builder = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
                .header("Content-Type", "application/json");
        var publisher = body == null
                ? HttpRequest.BodyPublishers.noBody()
                : HttpRequest.BodyPublishers.ofString(body);
        return http.send(builder.method(method, publisher).build(), HttpResponse.BodyHandlers.ofString());
    }

    private long id(String resource) {
        var match = java.util.regex.Pattern.compile("\"id\":(\\d+)").matcher(resource);
        assertTrue(match.find(), "La respuesta debe incluir el id: " + resource);
        return Long.parseLong(match.group(1));
    }
}
