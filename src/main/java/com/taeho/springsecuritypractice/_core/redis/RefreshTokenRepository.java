package com.taeho.springsecuritypractice._core.redis;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
    Optional<RefreshToken> findById(String refreshToken);
    Optional<RefreshToken> findByAccessToken(String accessToken);
}
