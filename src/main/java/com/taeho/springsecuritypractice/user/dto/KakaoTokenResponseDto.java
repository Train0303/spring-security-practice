package com.taeho.springsecuritypractice.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class KakaoTokenResponseDto {
    private String token_type;
    private String access_token;
    private String id_token;
    private Integer expires_in;
    private String refresh_token;
    private Integer refresh_token_expires_in;
    private String scope;

    @Builder
    public KakaoTokenResponseDto(String token_type, String access_token, String id_token, Integer expires_in, String refresh_token, Integer refresh_token_expires_in, String scope) {
        this.token_type = token_type;
        this.access_token = access_token;
        this.id_token = id_token;
        this.expires_in = expires_in;
        this.refresh_token = refresh_token;
        this.refresh_token_expires_in = refresh_token_expires_in;
        this.scope = scope;
    }
}
