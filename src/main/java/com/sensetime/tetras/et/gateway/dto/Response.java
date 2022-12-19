package com.sensetime.tetras.et.gateway.dto;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.Optional;

/**
 * @author lixiaoming
 * @date 2022/12/8 14:24
 * 通用response对象
 */
@Data
public class Response<J> {

    private Integer code;

    private String msg;

    private J data;

    public static <T> Response<T> success(String msg, T data) {
        Response<T> response = success(data);
        response.msg = Optional.ofNullable(msg).orElse("成功");
        return response;
    }

    public static <T> Response<T> success(T data) {
        Response<T> response = success();
        response.setData(data);
        return response;
    }

    public static <T> Response<T> success() {
        Response<T> response = new Response<>();
        response.code = HttpStatus.OK.value();
        response.msg = ("成功");
        return response;
    }

    public static <T> Response<T> fail(Integer code, String msg) {
        Response<T> response = new Response<>();
        response.code = code;
        response.msg = Optional.ofNullable(msg).orElse("失败");
        return response;
    }

    public static <T> Response<T> fail(String msg) {
        Response<T> response = new Response<>();
        response.code = 500;
        response.msg = Optional.ofNullable(msg).orElse("失败");
        return response;
    }
}
