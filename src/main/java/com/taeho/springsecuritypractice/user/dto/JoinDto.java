package com.taeho.springsecuritypractice.user.dto;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
public class JoinDto {

    @NotEmpty
    @Email(regexp = "^[a-zA-Z0-9+-\\_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$",
            message = "이메일 형식을 맞춰주세요")
    String email;

    @NotEmpty
    @Size(min = 2, max = 8,
            message = "2글자 이상 8글자 이하로 작성해주세요.")
    String username;

    @NotEmpty
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*\\W).{8,20}$",
            message = "비밀번호는 영문과 특수문자 숫자를 포함하며 8자 이상 20자 이하이어야 합니다.")
    String password;

    @Builder
    public JoinDto(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }
}
