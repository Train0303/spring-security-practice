package com.taeho.springsecuritypractice.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LoginRespDto {
    private Long userId;
    private String accessToken;
    private String refreshToken;

    @Builder
    public LoginRespDto(Long userId, String accessToken, String refreshToken) {
        this.userId = userId;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

}
