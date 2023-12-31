package com.taeho.springsecuritypractice._core.security;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.taeho.springsecuritypractice._core.errors.exeption.Exception400;
import com.taeho.springsecuritypractice._core.redis.BlackListTokenRepository;
import com.taeho.springsecuritypractice._core.redis.BlackListTokenService;
import com.taeho.springsecuritypractice.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class JwtAuthenticationFilter extends BasicAuthenticationFilter {

    private final BlackListTokenService blackListTokenService;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, BlackListTokenService blackListTokenService){
        super(authenticationManager);
        this.blackListTokenService = blackListTokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String jwt = request.getHeader(JwtProvider.HEADER);

        if (jwt == null){
            chain.doFilter(request, response);
            return;
        }

        if (!jwt.startsWith("Bearer ")) {
            log.error("잘못된 토큰");
            throw new JWTDecodeException("토큰 형식이 잘못되었습니다.");
        }

        if (blackListTokenService.isExistBlackListToken(jwt)) {
            log.warn("블랙리스트에 등록된 토큰");
            throw new JWTVerificationException("블랙리스트에 등록된 토큰입니다.");
        }

        try{
            DecodedJWT decodedJWT = JwtProvider.verify(jwt);
            Long id = decodedJWT.getClaim("id").asLong();
            String roles = decodedJWT.getClaim("role").asString();
            System.out.println("roles : " + roles);
            User user = User.builder().id(id).roles(roles).build();
            CustomUserDetails userDetails = new CustomUserDetails(user);
            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            userDetails.getPassword(),
                            userDetails.getAuthorities()
                    );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("디버그 : 인증 객체 만들어짐");
        } catch (SignatureVerificationException sve) {
            log.error("토큰 검증 실패");
            throw new JWTVerificationException("토큰 검증이 실패했습니다.");
        } catch (TokenExpiredException tee) {
            log.error("토큰 만료 됨");
//            throw new JWTVerificationException("만료된 토큰 입니다.");
        } catch (JWTDecodeException jde) {
            log.error("잘못된 토큰");
            throw new JWTDecodeException("토큰 형식이 잘못되었습니다.");
        }

        chain.doFilter(request, response);
    }
}
