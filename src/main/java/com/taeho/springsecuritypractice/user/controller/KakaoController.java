package com.taeho.springsecuritypractice.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taeho.springsecuritypractice._core.errors.exeption.Exception400;
import com.taeho.springsecuritypractice.user.UserService;
import com.taeho.springsecuritypractice.user.dto.KakaoAccessTokenDto;
import com.taeho.springsecuritypractice.user.dto.KakaoTokenResponseDto;
import com.taeho.springsecuritypractice.user.dto.KakaoUserInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/kakao")
public class KakaoController {

    @Value("${oauth.kakao.client-id}")
    private String clientId;

    @Value("${oauth.kakao.redirect}")
    private String redirect;

    @Value("${oauth.kakao.secret}")
    private String secret;

    private final UserService userService;
    private final ObjectMapper om;

    @GetMapping("/login")
    public void kakaoLogin(HttpServletResponse response) throws IOException {
        String url = String.format("https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=%s&redirect_uri=%s", clientId, redirect);
        response.sendRedirect(url);
    }

    @GetMapping("/login/redirect")
    public String kakaoLoginRedirect(@RequestParam("code") String code) throws JsonProcessingException {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("grant_type", "authorization_code");
        parameters.add("client_id", clientId);
        parameters.add("redirect_url", redirect);
        parameters.add("code", code);
        parameters.add("client_secret", secret);

        String tokenUrl = "https://kauth.kakao.com/oauth/token";

        RestTemplate restTemplate = new RestTemplate();
        try{
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<?> httpRequestEntity = new HttpEntity<>(parameters, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(tokenUrl,httpRequestEntity, String.class);
            KakaoTokenResponseDto result = om.readValue(response.getBody(), KakaoTokenResponseDto.class);
            return "kakao " + result.getAccess_token();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return "failed";
    }

    @PostMapping("/api/login")
    public ResponseEntity<?> serverLogin(HttpServletRequest request) {
        String accessToken = request.getHeader("Kakao");
        if(accessToken == null || !accessToken.startsWith("kakao ")) {
            throw new Exception400("카카오 accessToken을 입력해주세요.");
        }

        accessToken = accessToken.replace("kakao ", "");

        // 이메일만 필요하기 때문에 아래 링크를 사용
        String getUserMeUrl = "https://kapi.kakao.com/v2/user/me?property_keys=[\"kakao_account.email\"]";
        RestTemplate restTemplate = new RestTemplate();
        try{
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            HttpEntity<?> httpRequestEntity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(getUserMeUrl, HttpMethod.GET, httpRequestEntity, String.class);
            KakaoUserInfoDto kakaoUserInfoDto = om.readValue(response.getBody(), KakaoUserInfoDto.class);
            System.out.println(kakaoUserInfoDto);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return ResponseEntity.ok("ok");
    }
}
