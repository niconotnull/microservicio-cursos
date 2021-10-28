package com.springboot.curso.controller;

import com.springboot.curso.entity.AlumnoEntity;
import com.springboot.curso.entity.CursoAlumnoEntity;
import com.springboot.curso.entity.CursoEntity;
import com.springboot.curso.entity.ExamenEntity;
import com.springboot.curso.excepcion.DBException;
import com.springboot.curso.generic.GenericController;
import com.springboot.curso.service.CursoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class CursoController extends GenericController<CursoEntity, CursoService> {

    @Autowired
    private CircuitBreakerFactory circuitBreakerFactory;

    @Value("${config.balanceador.test}")
    private String balanceadorTest;

    @GetMapping(value = "/listar")
    public ResponseEntity<?> findAll(){
        return  circuitBreakerFactory.create("cursos")
                .run(()-> {
                    try {
                        List<CursoEntity> cursos=  service.findAll().stream().map(c->{
                            c.getCursoAlumnos().forEach(ca->{
                                AlumnoEntity alumno = new AlumnoEntity();
                                alumno.setId(ca.getAlumnoId());
                                c.addAlumno(alumno);
                            });
                            return  c;
                        }).collect(Collectors.toList());
                        return new ResponseEntity<>(cursos, HttpStatus.OK);
                    }catch (DBException e){
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
                    }
                }, e->  new ResponseEntity<>(metodoAlternativo(), HttpStatus.OK));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> findById(@PathVariable(required = true) Integer id) {
        try {
            Optional<CursoEntity> c = Optional.ofNullable(service.findById(id));
            CursoEntity curso = c.get();

            if (!curso.getCursoAlumnos().isEmpty()) {
                List<Integer> ids = curso.getCursoAlumnos().stream().map(CursoAlumnoEntity::getAlumnoId).collect(Collectors.toList());
                System.out.println("ids : "+ids.toString());
                List<AlumnoEntity> alumnos = service.obtenerAlumnosXCurso(ids);
                System.out.println("Lista de alumnos : "+alumnos.toString());
                curso.setAlumnos(alumnos);
            }

            return new ResponseEntity<>(curso, HttpStatus.OK);
        } catch (DBException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping(value = "/paginacion")
    public ResponseEntity<?> findAll(Pageable pageable){
        try {
            Page<CursoEntity> cursos = service.findAll(pageable).map(curso->{
                curso.getCursoAlumnos().forEach(ca->{
                    AlumnoEntity alumno = new AlumnoEntity();
                    alumno.setId(ca.getId());
                    curso.addAlumno(alumno);
                });
                return  curso;
            });

            return new ResponseEntity<>(cursos, HttpStatus.OK);
        }catch (DBException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }


    @GetMapping(value = "/balanceador-test")
    public ResponseEntity<?> balanceadorTest() {
        Map<String,Object> response = new HashMap<>();
        response.put("balanceador",balanceadorTest);
        response.put("cursos", service.findAll());
        return ResponseEntity.ok().body(response);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<?> update(@Valid @RequestBody CursoEntity curso, BindingResult result, @PathVariable Integer id) {
        try {
            if (result.hasErrors()) {
                return this.validar(result);
            }

            Optional<CursoEntity> cur = Optional.ofNullable(this.service.findById(id));
            if (!cur.isPresent()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el registro con el id: " + id);
            }
            CursoEntity cursoDB = cur.get();
            cursoDB.setNombre(curso.getNombre());
            return new ResponseEntity<>(service.save(cursoDB), HttpStatus.CREATED);
        } catch (DBException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping(value = "/{id}/asignar-alumnos")
    public ResponseEntity<?> asignarAlumnos(@RequestBody List<AlumnoEntity>alumnos,  @PathVariable Integer id){
        try {
            Optional<CursoEntity> cursoRes = Optional.ofNullable(this.service.findById(id));
            if (!cursoRes.isPresent()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el curso con el id: " + id);
            }
            CursoEntity cursoDB = cursoRes.get();
            alumnos.forEach(c->{
                CursoAlumnoEntity cursoAlumno =  new CursoAlumnoEntity();
                cursoAlumno.setAlumnoId(c.getId());
                cursoAlumno.setCurso(cursoDB);
                cursoDB.addCursoAlumno(cursoAlumno);
            });

            return new ResponseEntity<>(service.save(cursoDB), HttpStatus.CREATED);
        } catch (DBException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping(value = "/{id}/eliminar-alumno")
    public ResponseEntity<?> eliminarAlumno(@RequestBody AlumnoEntity alumno,  @PathVariable Integer id){
        try {
            Optional<CursoEntity> cursoRes = Optional.ofNullable(this.service.findById(id));
            if (!cursoRes.isPresent()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el curso con el id: " + id);
            }
            CursoEntity cursoDB = cursoRes.get();
            CursoAlumnoEntity cursoAlumno = new CursoAlumnoEntity();
            cursoAlumno.setAlumnoId(alumno.getId());
            cursoDB.removeCursoAlumno(cursoAlumno);

            cursoDB.removeAlumno(alumno);

            return new ResponseEntity<>(service.save(cursoDB), HttpStatus.OK);
        } catch (DBException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }


    @PutMapping(value = "/{id}/asignar-examenes")
    public ResponseEntity<?> asignarExamenes(@RequestBody List<ExamenEntity> examenes, @PathVariable Integer id){
        try {
            Optional<CursoEntity> cursoRes = Optional.ofNullable(this.service.findById(id));
            if (!cursoRes.isPresent()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el curso con el id: " + id);
            }
            CursoEntity cursoDB = cursoRes.get();
            examenes.forEach(cursoDB::addExamen);

            return new ResponseEntity<>(service.save(cursoDB), HttpStatus.CREATED);
        } catch (DBException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping(value = "/{id}/eliminar-examen")
    public ResponseEntity<?> eliminarExamen(@RequestBody ExamenEntity examen,  @PathVariable Integer id){
        try {
            Optional<CursoEntity> cursoRes = Optional.ofNullable(this.service.findById(id));
            if (!cursoRes.isPresent()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el curso con el id: " + id);
            }
            CursoEntity cursoDB = cursoRes.get();
            cursoDB.removeExamen(examen);

            return new ResponseEntity<>(service.save(cursoDB), HttpStatus.NO_CONTENT);
        } catch (DBException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }


    @GetMapping(value = "/alumno/{id}")
    public ResponseEntity<?> findCursoByAlumnoId(@PathVariable Integer id) {
        try {
            System.out.println("buscando cursos");
            CursoEntity curso = service.findCursoByAlumnoId(id);
            if (curso != null) {
                List<Integer> examenesId = (List<Integer>) service.obtenerExamenesIdsConRespuestasAlumno(id);
                if (examenesId != null && examenesId.size() > 0) {
                    List<ExamenEntity> examenes = curso.getExamenes().stream().map(examen -> {
                        if (examenesId.contains(examen.getId())) {
                            examen.setRespondido(true);
                        }
                        return examen;
                    }).collect(Collectors.toList());
                    curso.setExamenes(examenes);
                }
            }
            return new ResponseEntity<>(curso, HttpStatus.OK);
        } catch (DBException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @DeleteMapping(value = "/eliminar-alumno-curso/{id}")
    public ResponseEntity<?> eliminarCursoAlumnoPorId(@PathVariable Integer id) {
        try {
            service.eliminarCursoAlumnoPorId(id);
            return ResponseEntity.noContent().build();
        } catch (DBException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }

    }


    public List<CursoEntity> metodoAlternativo(){
        CursoEntity curso = new CursoEntity();
        curso.setNombre("Este curso es del metodo alternativo");
        List<CursoEntity> list = new ArrayList<>();
        list.add(curso);
        return  list;
    }



}
