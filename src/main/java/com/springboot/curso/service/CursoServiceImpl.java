package com.springboot.curso.service;

import com.springboot.curso.client.AlumnoFeignClient;
import com.springboot.curso.client.RespuestaFeignClient;
import com.springboot.curso.entity.AlumnoEntity;
import com.springboot.curso.entity.CursoEntity;
import com.springboot.curso.excepcion.DBException;
import com.springboot.curso.excepcion.HttpException;
import com.springboot.curso.generic.GenericServiceImpl;
import com.springboot.curso.repository.CursoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CursoServiceImpl  extends GenericServiceImpl<CursoEntity, CursoRepository>  implements CursoService{

    @Autowired
    private RespuestaFeignClient client;

    @Autowired
    private AlumnoFeignClient clientAlumno;

    @Autowired
    private CursoRepository cursoRepository;


    @Override
    public List<CursoEntity> findAll() {
        try {
            return (List<CursoEntity>) cursoRepository.findAll();
        } catch (
                DataAccessException e) {
            throw new DBException(e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
        }
    }

    @Override
    public Page<CursoEntity> findAll(Pageable pageable) {
        return cursoRepository.findAllByOrderByIdDesc(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public CursoEntity findCursoByAlumnoId(Integer id) {
        try {
            return repository.findCursoByAlumnoId(id);
        } catch (
                DataAccessException e) {
            throw new DBException(e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
        }
    }

    @Override
    public Iterable<Integer> obtenerExamenesIdsConRespuestasAlumno(Integer alumnoId) {
        try {
            return client.obtenerExamenesIdsConRespuestasAlumno(alumnoId);
        }catch (HttpException e){
            throw new DBException(e.getMessage().concat(": ").concat(e.getLocalizedMessage()));
        }
    }

    @Override
    public List<AlumnoEntity> obtenerAlumnosXCurso(Iterable<Integer> ids) {
      try{
            return  clientAlumno.obtenerAlumnosXCurso(ids);
      }catch (HttpException e){
          throw new DBException(e.getMessage().concat(": ").concat(e.getLocalizedMessage()));
      }
    }

    @Override
    @Transactional
    public void eliminarCursoAlumnoPorId(Integer id) {
        try {
            repository.eliminarCursoAlumnoPorId(id);
        } catch (
                DataAccessException e) {
            throw new DBException(e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
        }
    }
}
