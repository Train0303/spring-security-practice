package com.taeho.springsecuritypractice.service;

import com.taeho.springsecuritypractice._core.errors.exeption.Exception400;
import com.taeho.springsecuritypractice._core.errors.exeption.Exception404;
import com.taeho.springsecuritypractice._core.errors.exeption.Exception500;
import com.taeho.springsecuritypractice._core.redis.BlackListTokenService;
import com.taeho.springsecuritypractice._core.redis.RefreshTokenService;
import com.taeho.springsecuritypractice._core.security.JwtProvider;
import com.taeho.springsecuritypractice.user.User;
import com.taeho.springsecuritypractice.user.UserMapper;
import com.taeho.springsecuritypractice.user.UserRepository;
import com.taeho.springsecuritypractice.user.UserService;
import com.taeho.springsecuritypractice.user.dto.JoinDto;
import com.taeho.springsecuritypractice.user.dto.LoginDto;
import com.taeho.springsecuritypractice.user.dto.LoginRespDto;
import com.taeho.springsecuritypractice.user.dto.ReissueRespDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Mock
    private BlackListTokenService blackListTokenService;

    @Mock
    private UserMapper userMapper;

    private BCryptPasswordEncoder testPasswordEncoder;
    private UserMapper testUserMapper;

    @BeforeEach
    public void setUp() {
        testPasswordEncoder = new BCryptPasswordEncoder();
        testUserMapper = new UserMapper(testPasswordEncoder);
        JwtProvider.REFRESH_SECRET = "userServiceTestRefresh";
        JwtProvider.ACCESS_SECRET = "userServiceTestAccess";
    }

    @DisplayName("회원가입 테스트")
    @Test
    public void user_join_test() {
        // given
        JoinDto joinDto = new JoinDto("test@gmail.com", "test", "test12!");
        User user = testUserMapper.joinDtoToUser(joinDto);

        // stub
        given(userMapper.joinDtoToUser(joinDto)).willReturn(user);
        given(userRepository.findByEmail(any())).willReturn(Optional.empty());

        // when
        userService.join(joinDto);

        // then
    }

    @DisplayName("회원가입 테스트 실패: 이미 회원가입한 이메일")
    @Test
    public void user_join_test_fail_already_join_email() {
        // given
        JoinDto joinDto = new JoinDto("test@gmail.com", "test", "test12!");
        User user = testUserMapper.joinDtoToUser(joinDto);

        // stub
        given(userRepository.findByEmail(any())).willReturn(Optional.of(user));

        // when
        Exception exception = assertThrows(Exception400.class, () -> userService.join(joinDto));

        // then
        assertEquals("이미 회원가입한 이메일입니다.", exception.getMessage());
    }

    @DisplayName("회원가입 테스트 실패: 저장 중 db에러")
    @Test
    public void user_join_test_fail_save_db_error() {
        // given
        JoinDto joinDto = new JoinDto("test@gmail.com", "test", "test12!");
        User user = testUserMapper.joinDtoToUser(joinDto);

        // stub
        given(userRepository.findByEmail(any())).willReturn(Optional.empty());
        given(userRepository.save(any())).willThrow(new RuntimeException("db error"));
        // when
        Exception exception = assertThrows(Exception500.class, () -> userService.join(joinDto));

        // then
        assertEquals("회원 저장 중 문제가 발생했습니다. : db error", exception.getMessage());
    }

    @DisplayName("로그인 테스트")
    @Test
    public void user_login_test() {
        // given
        LoginDto loginDto = new LoginDto("test@gmail.com", "test12!");
        User user = new User(1L, "test", "test@gmail.com", testPasswordEncoder.encode("test12!"), "ROLE_USER");

        // stub
        given(userRepository.findByEmail("test@gmail.com")).willReturn(Optional.of(user));
        given(passwordEncoder.matches(loginDto.getPassword(), user.getPassword())).willReturn(true);
        doNothing().when(refreshTokenService).saveRefreshToken(any(), any(), any());

        // when
        LoginRespDto resultDto = userService.login(loginDto);

        // then
        assertTrue(resultDto.getAccessToken().startsWith("Bearer "));
        assertTrue(resultDto.getRefreshToken().startsWith("Refresh "));
        assertEquals(1L, resultDto.getUserId());
    }

    @DisplayName("로그인 테스트 실패: 존재하지 않는 이메일")
    @Test
    public void user_login_test_fail_not_exist_email() {
        // given
        LoginDto loginDto = new LoginDto("test@gmail.com", "test12!");
        User user = new User(1L, "test", "test@gmail.com", testPasswordEncoder.encode("test12!"), "ROLE_USER");

        // stub
        given(userRepository.findByEmail("test@gmail.com")).willReturn(Optional.empty());

        // when
        Exception e = assertThrows(Exception404.class, () -> userService.login(loginDto));

        // then
        assertEquals("존재하지 않는 이메일입니다.", e.getMessage());
    }

    @DisplayName("로그인 테스트 실패: 틀린 비밀번호")
    @Test
    public void user_login_test_fail_mismatch_password() {
        // given
        LoginDto loginDto = new LoginDto("test@gmail.com", "test12!");
        User user = new User(1L, "test", "test@gmail.com", testPasswordEncoder.encode("test12!@"), "ROLE_USER");

        // stub
        given(userRepository.findByEmail("test@gmail.com")).willReturn(Optional.of(user));
        given(passwordEncoder.matches(any(), any())).willReturn(testPasswordEncoder.matches(loginDto.getPassword(), user.getPassword()));

        // when
        Exception e = assertThrows(Exception400.class, () -> userService.login(loginDto));

        // then
        assertEquals("비밀번호가 일치하지 않습니다.", e.getMessage());
    }

    @DisplayName("로그인 테스트 실패: 레디스 에러")
    @Test
    public void user_login_test_fail_redis_error() {
        // given
        LoginDto loginDto = new LoginDto("test@gmail.com", "test12!");
        User user = new User(1L, "test", "test@gmail.com", testPasswordEncoder.encode("test12!"), "ROLE_USER");

        // stub
        given(userRepository.findByEmail("test@gmail.com")).willReturn(Optional.of(user));
        given(passwordEncoder.matches(any(), any())).willReturn(testPasswordEncoder.matches(loginDto.getPassword(), user.getPassword()));
        doThrow(new Exception500("Refresh 토큰 저장 중 레디스 데이터베이스에 문제가 발생했습니다.")).when(refreshTokenService).saveRefreshToken(any(), any(), any());

        // when
        Exception e = assertThrows(Exception500.class, () -> userService.login(loginDto));

        // then
        assertEquals("Refresh 토큰 저장 중 레디스 데이터베이스에 문제가 발생했습니다.", e.getMessage());
    }

    @DisplayName("로그아웃 테스트 실패: Refresh 레디스 에러")
    @Test
    public void user_logout_test_fail_Refresh_redis_error() {
        // given

        // stub
        doThrow(new RuntimeException("Refresh 토큰 저장 중 레디스 데이터베이스에 문제가 발생했습니다.")).when(refreshTokenService)
                .deleteRefreshTokenByAccessToken(any());

        // when
        Exception result = assertThrows(Exception500.class, () -> userService.logout(any()));

        // then
        assertEquals("로그아웃 중 오류가 발생했습니다 : Refresh 토큰 저장 중 레디스 데이터베이스에 문제가 발생했습니다.", result.getMessage());
    }

    @DisplayName("로그아웃 테스트 실패: BlackList 레디스 에러")
    @Test
    public void user_logout_test_fail_BlackList_redis_error() {
        // given

        // stub
        doThrow(new RuntimeException("BlackList 저장 중 레디스 데이터베이스에 문제가 발생했습니다.")).when(blackListTokenService)
                .save(any());

        // when
        Exception result = assertThrows(Exception500.class, () -> userService.logout(any()));

        // then
        assertEquals("로그아웃 중 오류가 발생했습니다 : BlackList 저장 중 레디스 데이터베이스에 문제가 발생했습니다.", result.getMessage());
    }

    @DisplayName("토큰 재발급 테스트")
    @Test
    public void user_reissue_test() {
        // given
        User user = new User(1L, "test", "test@gmail.com", testPasswordEncoder.encode("test12!"), "ROLE_USER");
        String refreshToken = JwtProvider.createRefreshToken(user);

        // stub
        given(refreshTokenService.existRefreshToken(refreshToken)).willReturn(true);
        doNothing().when(refreshTokenService).deleteRefreshToken(refreshToken);
        doNothing().when(refreshTokenService).saveRefreshToken(any(), any(), any());

        // when
        ReissueRespDto resultDto = userService.reissue(refreshToken);

        // then
        assertTrue(resultDto.getAccessToken().startsWith("Bearer "));
        assertTrue(resultDto.getRefreshToken().startsWith("Refresh "));

    }

    @DisplayName("토큰 재발급 테스트 실패 : 검증되지 않은 refresh 토큰")
    @Test
    public void user_reissue_test_fail_not_verify_refreshToken() {
        // given
        User user = new User(1L, "test", "test@gmail.com", testPasswordEncoder.encode("test12!"), "ROLE_USER");
        String refreshToken = "notVerifyToken~";

        // stub

        // when
        Exception e = assertThrows(Exception400.class, () -> userService.reissue(refreshToken));

        // then
        assertEquals("토큰 검증에 실패했습니다.", e.getMessage());
    }

    @DisplayName("토큰 재발급 테스트 실패 : 레디스에 존재하지 않은 refresh 토큰")
    @Test
    public void user_reissue_test_fail_not_exist_refreshToken_in_redis() {
        // given
        User user = new User(1L, "test", "test@gmail.com", testPasswordEncoder.encode("test12!"), "ROLE_USER");
        String refreshToken = JwtProvider.createRefreshToken(user);

        // stub
        given(refreshTokenService.existRefreshToken(refreshToken)).willReturn(false);

        // when
        Exception e = assertThrows(Exception404.class, () -> userService.reissue(refreshToken));

        // then
        assertEquals("Refresh Token Not Found", e.getMessage());
    }
}