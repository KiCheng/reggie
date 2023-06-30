package com.Lijiacheng.exception;

/**
 * 自定义的业务异常
 * */
public class ServiceException extends RuntimeException{
    public ServiceException(String message){
        super(message);
    }

}
