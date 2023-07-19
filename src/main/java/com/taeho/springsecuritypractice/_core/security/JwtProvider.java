package com.taeho.springsecuritypractice._core.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.taeho.springsecuritypractice.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtProvider {
    public static final Long EXP = 1000L * 60 * 30; // 30분
    public static final Long REFRESH_EXP = 1000L * 3600 * 24 * 7; // 1주일
//    public static final Long EXP = 1000L; // 1초
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER = "Authorization";


    public static String SECRET;

    @Value("${jwt-secret-key}")
    public void setSECRET(String value) {
        SECRET = value;
    }

    public static String create(User user) {
        String jwt = JWT.create()
                .withSubject(user.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXP))
                .withClaim("id", user.getId())
                .withClaim("role", user.getRoles())
                .sign(Algorithm.HMAC512(SECRET));
        return TOKEN_PREFIX + jwt;
    }

    public static String createRefreshToken(User user) {
        String jwt = JWT.create()
                .withSubject(user.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_EXP))
                .withClaim("id", user.getId())
                .sign(Algorithm.HMAC512(SECRET));
        return jwt;
    }

    public static DecodedJWT verify(String jwt) throws SignatureVerificationException, TokenExpiredException {
        jwt = jwt.replace(TOKEN_PREFIX, "");
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(SECRET))
                .build().verify(jwt);
        return decodedJWT;
    }
}