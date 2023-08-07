package com.taeho.springsecuritypractice.user.dto.oauth.kakao;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter @ToString
public class KakaoAccessTokenDto {
    private String grant_type;
    private String client_id;
    private String redirect_url;
    private String code;
    private String client_secret;

    @Builder
    public KakaoAccessTokenDto(String grant_type, String client_id, String redirect_url, String code, String client_secret) {
        this.grant_type = grant_type;
        this.client_id = client_id;
        this.redirect_url = redirect_url;
        this.code = code;
        this.client_secret = client_secret;
    }
}
