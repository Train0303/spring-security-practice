package com.taeho.springsecuritypractice._core.security;

import com.taeho.springsecuritypractice._core.errors.exeption.Exception401;
import com.taeho.springsecuritypractice._core.errors.exeption.Exception403;
import com.taeho.springsecuritypractice._core.utils.FilterResponseUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final JwtExceptionFilter jwtExceptionFilter;
    private final FilterResponseUtils filterResponseUtils;

    @Bean
    public PasswordEncoder passwordEncoder() {return PasswordEncoderFactories.createDelegatingPasswordEncoder();}

//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
//        return authenticationConfiguration.getAuthenticationManager();
//    }

    public class CustomSecurityFilterManager extends AbstractHttpConfigurer<CustomSecurityFilterManager, HttpSecurity> {
        @Override
        public void configure(HttpSecurity builder) throws Exception {
            AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);

//            builder.addFilter(new CustomUsernamePasswordAuthenticationFilter(authenticationManager));
            builder.addFilter(new JwtAuthenticationFilter(authenticationManager));
            builder.addFilterBefore(jwtExceptionFilter, JwtAuthenticationFilter.class);
            super.configure(builder);
        }
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF 해제
        http.csrf().disable();

        // iframe거부
        http.headers().frameOptions().sameOrigin();

        // cors 재설정
        http.cors().configurationSource(corsConfigurationSource());

        // jSessionID 사용 거부(세션 사용X)
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // 폼 로그인 해제 (UsernamePasswordAuthenticationFilter 비활성화)
        http.formLogin().disable();

        // 폼 로그아웃 해제
        http.logout().disable();

        // 로그인 인증창 비활성화
        http.httpBasic().disable();

        http.apply(new CustomSecurityFilterManager());

        // 인증 실패 처리
        http.exceptionHandling().authenticationEntryPoint((request, response, authException) -> {
            log.warn("인증되지 않은 사용자가 자원에 접근하려 합니다 : "+authException.getMessage());
            filterResponseUtils.unAuthorizationRepsonse(response,
                    new Exception401("인증되지 않은 사용자입니다."));
        });

        // 권한 실패 처리
        http.exceptionHandling().accessDeniedHandler((request, response, accessDeniedException) -> {
            log.warn("권한이 없는 사용자가 자원에 접근하려 합니다 : "+accessDeniedException.getMessage());
            filterResponseUtils.forbiddenResponse(response,
                    new Exception403("권한이 없는 사용자입니다."));
        });

        // 인증, 권한 필터 설정
        http.authorizeRequests(
                authorize -> authorize.antMatchers("/test", "/logout").authenticated()
                        .anyRequest().permitAll());

        return http.build();
    }

    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*"); // GET, POST, PUT, DELETE 허용
        configuration.addAllowedOriginPattern("*"); // 모든 IP 주소 허용
        configuration.setAllowCredentials(true); // 클라이언트에서 쿠키 요청 허용
        configuration.addExposedHeader("Authorization");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
