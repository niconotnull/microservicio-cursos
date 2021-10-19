package com.springboot.curso.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "cursos")
@Getter
@Setter
public class CursoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty
    private String nombre;

    @Column(name = "create_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createAt;

    @JsonIgnoreProperties(value = {"curso"}, allowSetters = true)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "curso", cascade = CascadeType.ALL, orphanRemoval = true)
    // Por que es una relación bidireccional
    private List<CursoAlumnoEntity> cursoAlumnos;

    //@OneToMany(fetch = FetchType.LAZY)
    @Transient
    private List<AlumnoEntity> alumnos;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "curso_examenes", joinColumns = @JoinColumn(name = "curso_id"), inverseJoinColumns = @JoinColumn(name = "examen_id"), uniqueConstraints = {@UniqueConstraint(columnNames = {"curso_id", "examen_id"})})
    private List<ExamenEntity> examenes;

    @PrePersist
    public void prePersist() {
        this.createAt = new Date();
    }

    public void addAlumno(AlumnoEntity alumno) {
        this.alumnos.add(alumno);
    }

    public void removeAlumno(AlumnoEntity alumno) {
        this.alumnos.remove(alumno);
    }

    public void addExamen(ExamenEntity examen) {
        this.examenes.add(examen);
    }

    public void removeExamen(ExamenEntity examen) {
        this.examenes.remove(examen);
    }

    public void addCursoAlumno(CursoAlumnoEntity cursoAlumno) {
        this.cursoAlumnos.add(cursoAlumno);
    }

    public void removeCursoAlumno(CursoAlumnoEntity cursoAlumno) {
        this.cursoAlumnos.remove(cursoAlumno);
    }

    public CursoEntity() {
        this.alumnos = new ArrayList<>();
        this.examenes = new ArrayList<>();
        this.cursoAlumnos = new ArrayList<>();
    }

}
