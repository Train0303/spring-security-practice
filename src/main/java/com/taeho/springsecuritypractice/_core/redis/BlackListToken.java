package com.taeho.springsecuritypractice._core.redis;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.concurrent.TimeUnit;

@Getter
@RedisHash(value = "BlackList")
public class BlackListToken {

    @Id
    private String accessToken;

    @TimeToLive(unit = TimeUnit.MILLISECONDS)
    private Long expiraition;

    @Builder
    public BlackListToken(String accessToken, Long expiraition) {
        this.accessToken = accessToken;
        this.expiraition = expiraition;
    }
}
