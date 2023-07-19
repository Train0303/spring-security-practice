package com.taeho.springsecuritypractice._core.security;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taeho.springsecuritypractice._core.errors.exeption.Exception401;
import com.taeho.springsecuritypractice._core.utils.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component
public class JwtExceptionFilter extends OncePerRequestFilter {

    private final ObjectMapper om = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            filterChain.doFilter(request, response);
        }catch (JWTCreationException | JWTVerificationException e){
            setJwtExceptionResponse(request, response, e);
        }
    }

    private void setJwtExceptionResponse(HttpServletRequest request, HttpServletResponse response, Throwable exception) throws IOException {
        Exception401 e = new Exception401(exception.getMessage());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(e.status().value());
        response.getOutputStream().write(om.writeValueAsBytes(e.body()));
    }
}
