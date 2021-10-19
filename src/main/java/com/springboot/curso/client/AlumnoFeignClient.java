package com.springboot.curso.client;

import com.springboot.curso.entity.AlumnoEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name="microservicio-usuarios")
public interface AlumnoFeignClient {

    @GetMapping(value = "/alumnos-por-curso")
    List<AlumnoEntity> obtenerAlumnosXCurso(@RequestParam Iterable<Integer> ids);
}
