package com.taeho.springsecuritypractice._core.redis;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import javax.persistence.Id;

@Getter
@RedisHash(value = "jwtToken", timeToLive = 60*60*24*7)
public class RefreshToken {

    @Id
    private String id;

    private String refreshToken;

    private Long userId;

    @Builder
    public RefreshToken(String refreshToken, String id, Long userId) {
        this.id = id;
        this.refreshToken = refreshToken;
        this.userId = userId;
    }
}
