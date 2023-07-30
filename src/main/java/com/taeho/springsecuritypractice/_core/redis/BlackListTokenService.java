package com.taeho.springsecuritypractice._core.redis;

import com.taeho.springsecuritypractice._core.errors.exeption.Exception500;
import com.taeho.springsecuritypractice._core.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BlackListTokenService {

    private final BlackListTokenRepository blackListTokenRepository;

    public void save(String accessToken) {

        Long remainTime = JwtProvider.getRemainExpiration(accessToken);
        try {
            blackListTokenRepository.save(new BlackListToken(accessToken, remainTime));
        } catch(Exception e) {
            throw new Exception500("BlackList 저장 중 레디스 데이터베이스에 문제가 발생했습니다.");
        }
    }

    public boolean isExistBlackListToken(String accessToken) {
        return blackListTokenRepository.existsById(accessToken);
    }
}
