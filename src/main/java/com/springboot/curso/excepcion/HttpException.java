package com.springboot.curso.excepcion;

public class HttpException extends RuntimeException{

    public  HttpException(String message){
        super(message);
    }

}
