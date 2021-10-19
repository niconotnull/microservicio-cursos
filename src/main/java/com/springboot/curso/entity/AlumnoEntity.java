package com.springboot.curso.entity;

import lombok.Data;

import java.util.Date;

@Data
public class AlumnoEntity {

    private Integer id;

    private String nombre;

    private String apellido;

    private String email;

    private Date createAt;

    private String urlFoto;


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof AlumnoEntity)) {
            return false;
        }

        AlumnoEntity a = (AlumnoEntity) obj;

        return this.id != null && this.id.equals(a.getId());
    }

}
