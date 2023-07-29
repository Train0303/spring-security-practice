package com.taeho.springsecuritypractice.user;

import com.taeho.springsecuritypractice._core.errors.exeption.Exception401;
import com.taeho.springsecuritypractice._core.security.JwtProvider;
import com.taeho.springsecuritypractice._core.utils.ApiUtils;
import com.taeho.springsecuritypractice.user.dto.JoinDto;
import com.taeho.springsecuritypractice.user.dto.LoginDto;
import com.taeho.springsecuritypractice.user.dto.LoginRespDto;
import com.taeho.springsecuritypractice.user.dto.ReissueRespDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@Validated
public class UserController {

    private final UserService userService;

    @PostMapping("/join")
    public ResponseEntity<?> join(@Valid @RequestBody JoinDto joinDto) {
        userService.join(joinDto);
        return new ResponseEntity<>(ApiUtils.success("ok"), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDto loginDto, HttpServletResponse response) {
        LoginRespDto loginRespDto = userService.login(loginDto);
        response.setHeader(JwtProvider.HEADER, loginRespDto.getAccessToken());
        response.setHeader("refresh", loginRespDto.getRefreshToken());
        return new ResponseEntity<>(ApiUtils.success(loginRespDto), HttpStatus.CREATED);
    }

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = request.getHeader("refresh");
        if(refreshToken == null || !refreshToken.startsWith("Refresh "))
            throw new Exception401("Refresh 토큰을 입력해주세요.");

        ReissueRespDto resultDto = userService.reissue(refreshToken);
        response.setHeader(JwtProvider.HEADER, resultDto.getAccessToken());
        response.setHeader("refresh", resultDto.getRefreshToken());
        return ResponseEntity.ok(ApiUtils.success(resultDto));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String accessToken = request.getHeader(JwtProvider.HEADER);
        userService.logout(accessToken);
        return ResponseEntity.ok(ApiUtils.success(null));
    }

    @GetMapping("/test")
    public String testAuthentication() {
        return "ok";
    }
}
