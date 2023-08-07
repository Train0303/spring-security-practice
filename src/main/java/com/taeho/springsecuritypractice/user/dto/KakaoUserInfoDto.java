package com.taeho.springsecuritypractice.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter @ToString
public class KakaoUserInfoDto {
    private Long id;
    private LocalDateTime connected_at;
    private KakaoAccount kakao_account;

    @Builder
    public KakaoUserInfoDto(Long id, LocalDateTime connected_at, KakaoAccount kakao_account) {
        this.id = id;
        this.connected_at = connected_at;
        this.kakao_account = kakao_account;
    }

    @Getter
    public static class KakaoAccount {
        private boolean has_email;
        private boolean email_needs_agreement;
        private boolean is_email_valid;
        private boolean is_email_verified;
        private String email;

        @Builder
        public KakaoAccount(boolean has_email, boolean email_needs_agreement, boolean is_email_valid, boolean is_email_verified, String email) {
            this.has_email = has_email;
            this.email_needs_agreement = email_needs_agreement;
            this.is_email_valid = is_email_valid;
            this.is_email_verified = is_email_verified;
            this.email = email;
        }
    }
}
