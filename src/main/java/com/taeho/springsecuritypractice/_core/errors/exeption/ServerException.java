package com.taeho.springsecuritypractice._core.errors.exeption;

import com.taeho.springsecuritypractice._core.utils.ApiUtils;
import org.springframework.http.HttpStatus;

public abstract class ServerException extends RuntimeException{

    public ServerException(String message) {super(message);}

    public ApiUtils.ApiResult<?> body() {return null;}

    public HttpStatus status() { return null;}
}
