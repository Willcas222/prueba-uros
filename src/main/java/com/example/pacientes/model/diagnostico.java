package com.example.pacientes.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table (name = "diagnosticos")
public class diagnostico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre_diagnostico;
    private String descripcion_diagnostico;
    @Column(unique = true)
    private String codigo_diagnostico;

    public diagnostico() {
    }

    public diagnostico(String nombre_diagnostico, String descripcion_diagnostico) {
        this.nombre_diagnostico = nombre_diagnostico;
        this.descripcion_diagnostico = descripcion_diagnostico;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre_diagnostico() {
        return nombre_diagnostico;
    }



    public void setNombre_diagnostico(String nombre_diagnostico) {
        this.nombre_diagnostico = nombre_diagnostico;
    }



    public String getDescripcion_diagnostico() {
        return descripcion_diagnostico;
    }



    public void setDescripcion_diagnostico(String descripcion_diagnostico) {
        this.descripcion_diagnostico = descripcion_diagnostico;
    }



    public String getCodigo_diagnostico() {
        return codigo_diagnostico;
    }



    public void setCodigo_diagnostico(String codigo_diagnostico) {
        this.codigo_diagnostico = codigo_diagnostico;
    }


    @Override 
    public String toString() {
        return "Diagnostico{" +
                "nombre_diagnostico='" + nombre_diagnostico + '\'' +
                ", descripcion_diagnostico='" + descripcion_diagnostico + '\'' +
                ", codigo_diagnostico=" + codigo_diagnostico +
                '}';
    }
    



}