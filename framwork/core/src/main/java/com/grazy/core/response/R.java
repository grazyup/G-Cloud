package com.grazy.core.response;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.io.Serializable;

/**
 * @Author: grazy
 * @Date: 2024-01-18 1:19
 * @Description: 项目前后端数据响应对象
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class R<T> implements Serializable {

    private static final long serialVersionUID = -5197917128359885562L;

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String msg;

    /**
     * 响应数据
     */
    private T data;

    private R(Integer code){
        this.code = code;
    }

    private R(Integer code, String msg){
        this.code = code;
        this.msg = msg;
    }

    private R(Integer code, T data){
        this.code = code;
        this.data = data;
    }

    private R(Integer code, String msg, T data){
        this.code = code;
        this.msg = msg;
        this.data = data;
    }


    @JsonIgnore
    @JSONField(serialize = false)
    private boolean isSuccess(){
        return code.equals(ResponseCode.SUCCESS.getCode());
    }


    /**
     * 成功--只有默认成功状态码
     */
    public static <T> R<T> success(){
        return new R<>(ResponseCode.SUCCESS.getCode());
    }

    /**
     * 成功--自定义响应消息
     */
    public static <T> R<T> success(String msg){
        return new R<>(ResponseCode.SUCCESS.getCode(),msg);
    }

    /**
     * 成功--携带响应数据(无响应消息)
     */
    public static <T> R<T> data(T data){
        return new R<>(ResponseCode.SUCCESS. getCode(),ResponseCode.SUCCESS.getDesc(), data);
    }

    /**
     * 成功--自定义响应消息与携带响应数据
     */
    public static <T> R<T> success(String msg, T data){
        return new R<>(ResponseCode.SUCCESS.getCode(), msg, data);
    }

    /**
     * 失败--只有默认失败状态码与默认失败状态码描述消息
     */
    public static <T> R<T> fail(){
        return new R<>(ResponseCode.ERROR.getCode(),ResponseCode.ERROR.getDesc());
    }

    /**
     * 失败--自定义失败状态码与失败响应消息
     */
    public static <T> R<T> fail(Integer error_code, String error_msg){
        return new R<>(error_code,error_msg);
    }

    /**
     * 失败--自定义失败响应消息
     */
    public static <T> R<T> fail(String error_msg){
        return new R<>(ResponseCode.ERROR.getCode(),error_msg);
    }

    /**
     * 失败--使用响应状态码枚举（其他失败情况）
     */
    public static <T> R<T> fail(ResponseCode responseCode){
        return new R<>(responseCode.getCode(),responseCode.getDesc());
    }


}
