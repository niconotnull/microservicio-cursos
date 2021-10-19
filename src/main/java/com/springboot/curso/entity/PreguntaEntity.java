package com.springboot.curso.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Table(name = "preguntas")
@Getter
@Setter
public class PreguntaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String texto;

    @JsonIgnoreProperties(value = {"preguntas"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="examen_id")
    private ExamenEntity examen;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof PreguntaEntity)) {
            return false;
        }
        PreguntaEntity a = (PreguntaEntity) obj;
        return this.id != null && this.id.equals(a.getId());
    }
}
