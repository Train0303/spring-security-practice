package com.taeho.springsecuritypractice.user;

import com.taeho.springsecuritypractice._core.errors.exeption.Exception400;
import com.taeho.springsecuritypractice._core.errors.exeption.Exception401;
import com.taeho.springsecuritypractice._core.redis.RefreshTokenService;
import com.taeho.springsecuritypractice._core.security.JwtProvider;
import com.taeho.springsecuritypractice.user.dto.JoinDto;
import com.taeho.springsecuritypractice.user.dto.LoginDto;
import com.taeho.springsecuritypractice.user.dto.LoginRespDto;
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
            refreshTokenService.saveRefreshToken(jwt.replace("Bearer ", ""), refreshToken, user.getId());
            response.setHeader(JwtProvider.HEADER, jwt);
            return userMapper.userToLoginRespDto(user);
        } catch(Exception e) {
            System.out.println(e);
            throw new Exception401("토큰 생성 실패");
        }
    }

}
