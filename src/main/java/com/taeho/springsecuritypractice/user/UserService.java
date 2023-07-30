package com.taeho.springsecuritypractice.user;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.taeho.springsecuritypractice._core.errors.exeption.Exception400;
import com.taeho.springsecuritypractice._core.errors.exeption.Exception401;
import com.taeho.springsecuritypractice._core.errors.exeption.Exception404;
import com.taeho.springsecuritypractice._core.errors.exeption.Exception500;
import com.taeho.springsecuritypractice._core.redis.BlackListTokenService;
import com.taeho.springsecuritypractice._core.redis.RefreshTokenService;
import com.taeho.springsecuritypractice._core.security.JwtProvider;
import com.taeho.springsecuritypractice.user.dto.JoinDto;
import com.taeho.springsecuritypractice.user.dto.LoginDto;
import com.taeho.springsecuritypractice.user.dto.LoginRespDto;
import com.taeho.springsecuritypractice.user.dto.ReissueRespDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final BlackListTokenService blackListTokenService;
    private final UserMapper userMapper;

    @Transactional
    public void join(JoinDto joinDto) {
        userRepository.findByEmail(joinDto.getEmail()).ifPresent((User u) ->{
            throw new Exception400("이미 회원가입한 이메일입니다.");
        });

        User user = userMapper.joinDtoToUser(joinDto);
        try {
            userRepository.save(user);
        } catch(Exception e) {
            throw new Exception500("회원 저장 중 문제가 발생했습니다. : " + e.getMessage());
        }
    }

    @Transactional
    public LoginRespDto login(LoginDto loginDto) {
        User user = userRepository.findByEmail(loginDto.getEmail()).orElseThrow(
                () -> new Exception404("존재하지 않는 이메일입니다.")
        );

        if(!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())){
            throw new Exception400("비밀번호가 일치하지 않습니다.");
        }

        String jwt = JwtProvider.create(user);
        String refreshToken = JwtProvider.createRefreshToken(user);
        refreshTokenService.saveRefreshToken(refreshToken, jwt, user);
        return LoginRespDto.builder()
                .userId(user.getId())
                .refreshToken(refreshToken)
                .accessToken(jwt)
                .build();
    }

    @Transactional
    public void logout(String accessToken) {
        try {
            refreshTokenService.deleteRefreshTokenByAccessToken(accessToken);
            blackListTokenService.save(accessToken);
        } catch(Exception e) {
            System.out.println(e);
            throw new Exception500("로그아웃 중 오류가 발생했습니다(Redis에러)");
        }
    }

    @Transactional
    public ReissueRespDto reissue(String refreshToken) {
        DecodedJWT decodedJWT = getValidDecodedRefreshToken(refreshToken);

        if (!refreshTokenService.existRefreshToken(refreshToken)) throw new Exception404("Refresh Token Not Found");

        Long userId = decodedJWT.getClaim("id").asLong();
        String email = decodedJWT.getClaim("email").asString();
        String roles = decodedJWT.getClaim("role").asString();
        User user = User.builder().id(userId).email(email).roles(roles).build();

        refreshTokenService.deleteRefreshToken(refreshToken);
        String newAccessToken = JwtProvider.create(user);
        String newRefreshToken = JwtProvider.createRefreshToken(user);
        refreshTokenService.saveRefreshToken(newRefreshToken, newAccessToken, user);
        return ReissueRespDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    // ------ private ------
    private DecodedJWT getValidDecodedRefreshToken(String refreshToken) {
        try {
            return JwtProvider.verifyRefreshToken(refreshToken);
        } catch(Exception e) {
            System.out.println(e.getMessage());
            throw new Exception400("토큰 검증에 실패했습니다.");
        }
    }
}
