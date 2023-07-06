package com.Lijiacheng.common;

import com.Lijiacheng.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 统一异常处理器类
 * */
@RestControllerAdvice
@Slf4j
public class ProjectExceptionAdvice {
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public Result<String> doSqlException(SQLIntegrityConstraintViolationException ex){

        log.info(ex.getMessage());  // Duplicate entry 'lisi' for key 'employee.idx_username'

        if(ex.getMessage().contains("Duplicate entry")){
            String[] split = ex.getMessage().split(" ");
            String msg = split[2] + "已存在";
            return Result.error(msg);
        }

        return Result.error("未知错误");
    }

    /**
     * 异常处理方法
     * */
    @ExceptionHandler(ServiceException.class)
    public Result<String> doServiceException(ServiceException ex){
        log.info(ex.getMessage());
        return Result.error(ex.getMessage());
    }
}
