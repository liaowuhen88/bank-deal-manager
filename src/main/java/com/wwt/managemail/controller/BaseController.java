package com.wwt.managemail.controller;
import com.wwt.managemail.common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;


public abstract class BaseController {
    public final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler
    public Result handleException(Exception e) {
        logger.error("", e);
        return Result.error( e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result bindException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        String errorMesssage = "校验失败:";
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errorMesssage += fieldError.getDefaultMessage() + ", ";
        }
        return Result.error( errorMesssage);
    }

}
