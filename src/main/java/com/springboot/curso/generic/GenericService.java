package com.springboot.curso.generic;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GenericService<E> {

    E save(E entity);

    E findById(Integer id);

    void deleteById(Integer id);

}
