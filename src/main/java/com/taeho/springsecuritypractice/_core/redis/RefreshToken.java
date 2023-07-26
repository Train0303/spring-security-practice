package com.taeho.springsecuritypractice._core.redis;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@RedisHash(value = "refreshToken", timeToLive = 60*60*24*7)
public class RefreshToken {

    @Id // springframework id를 써야함
    private String refreshToken;
    private Long userId;
    private String email;

    @Indexed // 필드 값으로 데이터 찾을 수 있게 하는 어노테이션
    private String accessToken;

    @Builder
    public RefreshToken(String refreshToken, Long userId, String email, String accessToken) {
        this.refreshToken = refreshToken;
        this.userId = userId;
        this.email = email;
        this.accessToken = accessToken;
    }
}
