package com.taeho.springsecuritypractice.user.service;

import com.taeho.springsecuritypractice._core.redis.RefreshTokenService;
import com.taeho.springsecuritypractice._core.security.JwtProvider;
import com.taeho.springsecuritypractice.user.User;
import com.taeho.springsecuritypractice.user.UserRepository;
import com.taeho.springsecuritypractice.user.dto.LoginRespDto;
import com.taeho.springsecuritypractice.user.dto.oauth.OauthUserInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class OauthUserService {
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public LoginRespDto oauthLogin(OauthUserInfoDto userInfo) {
        User user = userRepository.findByEmail(userInfo.email()).orElseGet(
                () -> userRepository.save(User.builder()
                        .username(userInfo.id().toString())
                        .email(userInfo.email())
                        .password(passwordEncoder.encode("{bcrypt}" + UUID.randomUUID()))
                        .roles("ROLE_USER")
                        .provider(userInfo.provider())
                        .build()
                )
        );

        String accessToken = JwtProvider.create(user);
        String refreshToken = JwtProvider.createRefreshToken(user);
        refreshTokenService.saveRefreshToken(refreshToken, accessToken, user);

        return LoginRespDto.builder()
                .userId(user.getId())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
