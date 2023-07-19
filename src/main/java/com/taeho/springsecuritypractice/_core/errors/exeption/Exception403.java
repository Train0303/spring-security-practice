package com.taeho.springsecuritypractice._core.errors.exeption;


import com.taeho.springsecuritypractice._core.utils.ApiUtils;
import org.springframework.http.HttpStatus;

public class Exception403 extends ClientException{
    public Exception403(String message) {super(message);}

    @Override
    public ApiUtils.ApiResult<?> body() {return ApiUtils.error(getMessage(), HttpStatus.FORBIDDEN);}

    @Override
    public HttpStatus status() { return HttpStatus.FORBIDDEN;}
}
