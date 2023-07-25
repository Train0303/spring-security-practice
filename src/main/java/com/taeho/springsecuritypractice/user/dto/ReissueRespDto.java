package com.taeho.springsecuritypractice.user.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
public class ReissueRespDto {
    private String accessToken;
    private String refreshToken;

    @Builder
    public ReissueRespDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
