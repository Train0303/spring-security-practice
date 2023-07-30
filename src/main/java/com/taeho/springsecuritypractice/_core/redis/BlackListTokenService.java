package com.taeho.springsecuritypractice._core.redis;

import com.taeho.springsecuritypractice._core.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BlackListTokenService {

    private final BlackListTokenRepository blackListTokenRepository;

    public void save(String accessToken) {
        Long remainTime = JwtProvider.getRemainExpiration(accessToken);
        if(remainTime > 0)
            blackListTokenRepository.save(new BlackListToken(accessToken, remainTime));
    }

    public boolean isExistBlackListToken(String accessToken) {
        return blackListTokenRepository.existsById(accessToken);
    }
}
