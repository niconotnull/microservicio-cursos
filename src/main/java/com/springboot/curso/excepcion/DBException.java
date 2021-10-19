package com.springboot.curso.excepcion;

public class DBException extends  RuntimeException{

    public DBException(String message){
        super(message);
    }
}
