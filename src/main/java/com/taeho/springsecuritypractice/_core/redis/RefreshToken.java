package com.taeho.springsecuritypractice._core.redis;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import javax.persistence.Id;

@Getter
@RedisHash(value = "refreshToken", timeToLive = 60*60*24*7)
public class RefreshToken {

    @Id
    private String id;
    private Long userId;
    private String email;

    @Builder
    public RefreshToken(String id, Long userId, String email) {
        this.id = id;
        this.userId = userId;
        this.email = email;
    }
}
