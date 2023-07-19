package com.taeho.springsecuritypractice._core.errors.exeption;

import com.taeho.springsecuritypractice._core.utils.ApiUtils;
import org.springframework.http.HttpStatus;

public class Exception500 extends ServerException{
    public Exception500(String message) {super(message);}

    @Override
    public ApiUtils.ApiResult<?> body() {return ApiUtils.error(getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);}

    @Override
    public HttpStatus status() { return HttpStatus.INTERNAL_SERVER_ERROR;}
}
