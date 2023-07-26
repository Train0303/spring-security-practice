package com.taeho.springsecuritypractice.user;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.taeho.springsecuritypractice._core.errors.exeption.Exception400;
import com.taeho.springsecuritypractice._core.errors.exeption.Exception401;
import com.taeho.springsecuritypractice._core.errors.exeption.Exception404;
import com.taeho.springsecuritypractice._core.errors.exeption.Exception500;
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
    private final UserMapper userMapper;

    @Transactional
    public void join(JoinDto joinDto) {
        userRepository.findByEmail(joinDto.getEmail()).ifPresent((User u) ->{
            throw new Exception400("이미 회원가입한 이메일입니다.");
        });

        User user = userMapper.joinDtoToUser(joinDto);

        userRepository.save(user);
    }

    public LoginRespDto login(LoginDto loginDto, HttpServletResponse response) {
        User user = userRepository.findByEmail(loginDto.getEmail()).orElseThrow(
                () -> new Exception400("존재하지 않는 이메일입니다.")
        );

        if(!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())){
            throw new Exception400("비밀번호가 일치하지 않습니다.");
        }

        try {
            String jwt = JwtProvider.create(user);
            String refreshToken = JwtProvider.createRefreshToken(user);
            refreshTokenService.saveRefreshToken(refreshToken, jwt, user);
            response.setHeader(JwtProvider.HEADER, jwt);
            response.setHeader("refresh", refreshToken);
            return userMapper.userToLoginRespDto(user,jwt,refreshToken);
        } catch(Exception e) {
            System.out.println(e);
            throw new Exception401("토큰 생성 실패");
        }
    }

    public void logout(String accessToken) {
        try {
            refreshTokenService.deleteRefreshTokenByAccessToken(accessToken);
        } catch(Exception e) {
            System.out.println(e);
            throw new Exception500("로그아웃 중 오류가 발생했습니다(Redis에러)");
        }
    }

    @Transactional
    public ReissueRespDto reissue(String refreshToken) {
        DecodedJWT decodedJWT;
        try {
            decodedJWT = JwtProvider.verifyRefreshToken(refreshToken);
        } catch(Exception e) {
            throw new Exception401(e.getMessage());
        }

        if (!refreshTokenService.existRefreshToken(refreshToken)) throw new Exception404("Refresh Token Not Found");

        Long userId = decodedJWT.getClaim("id").asLong();
        String email = decodedJWT.getClaim("email").asString();
        String roles = decodedJWT.getClaim("role").asString();
        User user = User.builder()
                .id(userId)
                .email(email)
                .roles(roles)
                .build();

        refreshTokenService.deleteRefreshToken(refreshToken);
        String accessToken = JwtProvider.create(user);
        String newRefreshToken = JwtProvider.createRefreshToken(user);
        refreshTokenService.saveRefreshToken(refreshToken, accessToken, user);
        return ReissueRespDto.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken)
                .build();
    }
}
