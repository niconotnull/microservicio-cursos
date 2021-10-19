package com.springboot.curso.service;

import com.springboot.curso.entity.AlumnoEntity;
import com.springboot.curso.entity.CursoEntity;
import com.springboot.curso.generic.GenericService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CursoService extends GenericService<CursoEntity> {

    CursoEntity findCursoByAlumnoId(Integer id);

    Iterable<Integer> obtenerExamenesIdsConRespuestasAlumno( Integer alumnoId);

    List<AlumnoEntity> obtenerAlumnosXCurso(Iterable<Integer> ids);

    void eliminarCursoAlumnoPorId(Integer id);

    List<CursoEntity> findAll();

    Page<CursoEntity> findAll(Pageable pageable);

}
