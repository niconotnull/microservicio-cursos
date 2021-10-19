package com.springboot.curso.repository;

import com.springboot.curso.entity.CursoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CursoRepository extends PagingAndSortingRepository<CursoEntity, Integer> {

    @Query("select c from CursoEntity c join fetch c.cursoAlumnos a  where a.alumnoId=?1")
    CursoEntity findCursoByAlumnoId(Integer id);


    @Modifying
    @Query("delete from CursoAlumnoEntity ca where ca.alumnoId=?1")
    void eliminarCursoAlumnoPorId(Integer id);


   Page<CursoEntity> findAllByOrderByIdDesc(Pageable pageable);

}
