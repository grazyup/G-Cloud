package com.grazy.web.handler;

import com.grazy.core.exception.GCloudBusinessException;
import com.grazy.core.response.R;
import com.grazy.core.response.ResponseCode;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

/**
 * @Author: grazy
 * @Date: 2024-01-22 17:42
 * @Description: 全局异常处理器
 */

@RestControllerAdvice
public class webExceptionHandler {

    /**
     * 配置自定义异常拦截器
     */
    @ExceptionHandler(value = GCloudBusinessException.class)
    public R<String> GCloudBusinessExceptionHandler(GCloudBusinessException e){
        return R.fail(e.getCode(), e.getMsg());
    }


    /**
     * 配置参数校验失败异常
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R<String> MethodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e){
       ObjectError objectError = e.getBindingResult().getAllErrors().stream().findFirst().get();
       return R.fail(ResponseCode.ERROR_PARAM.getCode(),objectError.getDefaultMessage());
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public R<String> constraintViolationExceptionHandler(ConstraintViolationException e) {
        ConstraintViolation constraintViolation = e.getConstraintViolations().stream().findFirst().get();
        return R.fail(ResponseCode.ERROR_PARAM.getCode(), constraintViolation.getMessage());
    }

    /**
     * 参数校验出现绑定错误抛出的异常
     */
    @ExceptionHandler(value = BindException.class)
    public R<String> bindExceptionHandler(BindException e) {
        FieldError fieldError = e.getBindingResult().getFieldErrors().stream().findFirst().get();
        return R.fail(ResponseCode.ERROR_PARAM.getCode(), fieldError.getDefaultMessage());
    }


    /**
     * RequestParam注解抛出的异常处理器
     */
    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public R<String> missingServletRequestParameterExceptionHandler(MissingServletRequestParameterException e) {
        return R.fail(ResponseCode.ERROR_PARAM.getCode(), ResponseCode.ERROR_PARAM.getDesc());
    }


    /**
     * 非法状态异常
     */
    @ExceptionHandler(value = IllegalStateException.class)
    public R<String> illegalStateExceptionHandler(IllegalStateException e) {
        return R.fail(ResponseCode.ERROR_PARAM.getCode(), ResponseCode.ERROR_PARAM.getDesc());
    }


    /**
     * 运行时异常
     */
    @ExceptionHandler(value = RuntimeException.class)
    public R<String> runtimeExceptionHandler(RuntimeException e) {
        e.printStackTrace();
        return R.fail(ResponseCode.ERROR.getCode(), e.getMessage());
    }

}
