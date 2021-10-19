package com.springboot.curso.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "cursos_alumnos")
@Getter
@Setter
public class CursoAlumnoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "alumno_id", unique = true)
    private Integer alumnoId;

    @JsonIgnoreProperties(value={"cursoAlumnos"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id")
    private CursoEntity curso; // Esta es la contraparte que se mapea en CursoEntity (mappedBy = "curso")


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof CursoAlumnoEntity)) {
            return false;
        }

        CursoAlumnoEntity a = (CursoAlumnoEntity) obj;

        return this.alumnoId != null && this.alumnoId.equals(a.getAlumnoId());
    }
}
