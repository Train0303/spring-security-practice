package com.taeho.springsecuritypractice._core.redis;

import com.taeho.springsecuritypractice.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Service
@Transactional
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public void saveRefreshToken(String refreshToken, String accessToken, User user) {
        refreshTokenRepository.save(RefreshToken.builder()
                .refreshToken(refreshToken)
                .userId(user.getId())
                .email(user.getEmail())
                .accessToken(accessToken)
                .build());
    }

    public void deleteRefreshToken(String refreshToken) {
        refreshTokenRepository.deleteById(refreshToken);
    }

    public boolean existRefreshToken(String refreshToken) {
        return refreshTokenRepository.existsById(refreshToken);
    }

    public void deleteRefreshTokenByAccessToken(String accessToken) {
        refreshTokenRepository.findByAccessToken(accessToken)
                .ifPresent(refreshTokenRepository::delete);
    }
}
