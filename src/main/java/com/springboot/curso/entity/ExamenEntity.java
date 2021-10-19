package com.springboot.curso.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="examenes")
@Getter
public class ExamenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty
    private  String nombre;

    @Column(name = "create_at")
    private Date createAt;

    @JsonIgnoreProperties(value = {"examen", "handler", "hibernateLazyInitializer"}, allowSetters = true)
    @OneToMany(mappedBy = "examen", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PreguntaEntity> preguntas;

    @JsonIgnoreProperties(value={"handler", "hibernateLazyInitializer"})
    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    private  AsignaturaEntity asignaturaPadre;

    @JsonIgnoreProperties(value={"handler", "hibernateLazyInitializer"})
    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    private  AsignaturaEntity asignaturaHija;

    @Transient
    private boolean respondido;

    @PrePersist
    public  void prePersist(){
        this.createAt = new Date();
    }

    public ExamenEntity() {
        this.preguntas = new ArrayList<>();
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public void setRespondido(boolean respondido) {
        this.respondido = respondido;
    }

    public void setPreguntas(List<PreguntaEntity> preguntas) {
        this.preguntas.clear();
        preguntas.forEach(this::addPregunta);
    }

    public void addPregunta(PreguntaEntity pregunta){
        this.preguntas.add(pregunta);
        pregunta.setExamen(this);
    }

    public void removePregunta(PreguntaEntity pregunta){
        this.preguntas.remove(pregunta);
        pregunta.setExamen(null);
    }

}