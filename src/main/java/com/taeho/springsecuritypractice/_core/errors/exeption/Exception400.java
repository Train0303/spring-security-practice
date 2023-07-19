package com.taeho.springsecuritypractice._core.errors.exeption;


import com.taeho.springsecuritypractice._core.utils.ApiUtils;
import org.springframework.http.HttpStatus;

public class Exception400 extends ClientException{
    public Exception400(String message) {super(message);}

    @Override
    public ApiUtils.ApiResult<?> body() {return ApiUtils.error(getMessage(), HttpStatus.BAD_REQUEST);}

    @Override
    public HttpStatus status() { return HttpStatus.BAD_REQUEST;}
}
