package com.taeho.springsecuritypractice.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LoginRespDto {
    private Long userId;

    @Builder
    public LoginRespDto(Long userId) {
        this.userId = userId;
    }
}
