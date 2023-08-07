package com.taeho.springsecuritypractice.user.dto.oauth;

public interface OauthUserInfoDto {
    String email();
    String provider();
    Long id();
}
