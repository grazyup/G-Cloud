package com.grazy.core.exception;

import com.grazy.core.response.ResponseCode;
import lombok.Data;

/**
 * @Author: grazy
 * @Date: 2024-01-18 2:18
 * @Description: 自定义异常类
 */

@Data
public class GCloudBusinessException extends RuntimeException{

    /**
     * 异常状态码
     */
    private Integer code;

    /**
     * 异常信息
     */
    private String msg;


    /**
     * 默认异常状态码与错误信息
     */
    public GCloudBusinessException(ResponseCode responseCode){
        this.code = responseCode.getCode();
        this.msg = responseCode.getDesc();
    }

    /**
     * 自定义异常状态码与异常信息
     */
    public GCloudBusinessException(Integer exception_code, String exception_msg){
        this.code = exception_code;
        this.msg = exception_msg;
    }

    /**
     * 自定义异常信息
     */
    public GCloudBusinessException(String exception_msg){
        this.code = ResponseCode.ERROR.getCode();
        this.msg = exception_msg;
    }

    /**
     * 默认普通错误异常
     */
    public GCloudBusinessException() {
        this.code = ResponseCode.ERROR.getCode();
        this.msg = ResponseCode.ERROR.getDesc();
    }


}
