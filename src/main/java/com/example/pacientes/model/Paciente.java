package com.example.pacientes.model;

import java.time.LocalDate;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "pacientes")
@Getter
@Setter
public class Paciente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String apellido;
    private int edad;
    @Column(name = "fecha_nacimiento", updatable = true, insertable = true)
    private LocalDate fechaNacimiento;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "diagnostico_id", referencedColumnName = "id")
    private diagnostico diagnostico;

    @ManyToOne
    @JoinColumn(name = "municipio_id")
    private Municipio municipio;

    public Paciente() {
    }

    public Paciente(String nombre, String apellido, int edad, LocalDate fechaNacimiento, Municipio municipio) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.edad = edad;
        this.fechaNacimiento = fechaNacimiento;
        this.municipio = municipio;
        this.diagnostico = null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public Municipio getMunicipio() {
        return municipio;
    }

    public void setMunicipio(Municipio municipio) {
        this.municipio = municipio;
    }

    public void setDiagnostico(diagnostico diagnostico) {
        this.diagnostico = diagnostico;
    }

    public diagnostico getDiagnostico() {
        return diagnostico;
    }

    public void mostrarHistorial() {
        System.out.println("Paciente: " + nombre);
        if (diagnostico != null) {
            System.out.println("Diagnóstico: " + diagnostico);
        } else {
            System.out.println("Diagnóstico: Ninguno asignado todavía.");
        }
    }
}