package com.springboot.curso.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="microservicio-respuestas")
public interface RespuestaFeignClient {

    @GetMapping(value= "/alumno/{alumnoId}/examenes-respondidos")
    Iterable<Integer> obtenerExamenesIdsConRespuestasAlumno(@PathVariable Integer alumnoId);


}
