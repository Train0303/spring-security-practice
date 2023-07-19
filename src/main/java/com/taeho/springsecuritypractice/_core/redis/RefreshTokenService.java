package com.taeho.springsecuritypractice._core.redis;

import com.taeho.springsecuritypractice._core.errors.exeption.Exception400;
import com.taeho.springsecuritypractice._core.errors.exeption.Exception404;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public void saveRefreshToken(String accessToken, String refreshToken, Long userId) {
        refreshTokenRepository.save(RefreshToken.builder()
                .id(accessToken)
                .refreshToken(refreshToken)
                .userId(userId)
                .build());

    }
}
