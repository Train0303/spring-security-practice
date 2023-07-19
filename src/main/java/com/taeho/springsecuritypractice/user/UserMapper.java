package com.taeho.springsecuritypractice.user;

import com.taeho.springsecuritypractice.user.dto.JoinDto;
import com.taeho.springsecuritypractice.user.dto.LoginDto;
import com.taeho.springsecuritypractice.user.dto.LoginRespDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {
    private final PasswordEncoder passwordEncoder;

    public User joinDtoToUser(JoinDto joinDto) {
        return User.builder()
                .email(joinDto.getEmail())
                .username(joinDto.getUsername())
                .password(passwordEncoder.encode(joinDto.getPassword()))
                .roles("ROLE_USER")
                .build();
    }

    public LoginRespDto userToLoginRespDto(User user) {
        return LoginRespDto.builder()
                .userId(user.getId())
                .build();
    }
}
