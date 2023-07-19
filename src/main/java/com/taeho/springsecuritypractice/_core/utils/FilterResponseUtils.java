package com.taeho.springsecuritypractice._core.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taeho.springsecuritypractice._core.errors.exeption.Exception401;
import com.taeho.springsecuritypractice._core.errors.exeption.Exception403;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.http.HttpResponse;


@Component
@RequiredArgsConstructor
public class FilterResponseUtils {

    private final ObjectMapper om;

    public void unAuthorizationRepsonse(HttpServletResponse response, Exception401 e) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(e.status().value());
        response.getWriter().println(om.writeValueAsString(e.body()));
    }

    public void forbiddenResponse(HttpServletResponse response, Exception403 e) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(e.status().value());
        response.getWriter().println(om.writeValueAsString(e.body()));
    }
}
