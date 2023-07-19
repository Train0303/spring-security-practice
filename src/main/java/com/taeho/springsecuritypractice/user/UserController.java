package com.taeho.springsecuritypractice.user;

import com.taeho.springsecuritypractice._core.utils.ApiUtils;
import com.taeho.springsecuritypractice.user.dto.JoinDto;
import com.taeho.springsecuritypractice.user.dto.LoginDto;
import com.taeho.springsecuritypractice.user.dto.LoginRespDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
        LoginRespDto loginRespDto = userService.login(loginDto, response);
        return new ResponseEntity<>(ApiUtils.success(loginRespDto), HttpStatus.CREATED);
    }

    @GetMapping("/test")
    public String testAuthentication() {
        return "ok";
    }
}
