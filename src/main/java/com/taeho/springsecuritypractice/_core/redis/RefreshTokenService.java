package com.taeho.springsecuritypractice._core.redis;

import com.taeho.springsecuritypractice._core.errors.exeption.Exception500;
import com.taeho.springsecuritypractice.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public void saveRefreshToken(String refreshToken, String accessToken, User user) {
        try {
            refreshTokenRepository.save(RefreshToken.builder()
                    .refreshToken(refreshToken)
                    .userId(user.getId())
                    .email(user.getEmail())
                    .accessToken(accessToken)
                    .build());
        }catch (Exception e) {
            throw new Exception500("Refresh 토큰 저장 중 레디스 데이터베이스에 문제가 발생했습니다.");
        }
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
