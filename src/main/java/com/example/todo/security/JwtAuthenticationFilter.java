package com.example.todo.security;

import com.example.todo.service.TokenBlacklistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

// JWT를 검사하고 인증 처리를 해주는 필터
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // OncePerRequestFilter는 요청 1번마다 한 번만 실행되는 필터

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenBlacklistService tokenBlacklistService;

    // 요청이 들어올 때마다 실행되는 함수
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 요청 헤더 꺼내기
        String authHeader = request.getHeader("Authorization");
        // bearer 토큰 형식인지 판단
        if(authHeader != null && authHeader.startsWith("Bearer ")) {
           String token = authHeader.substring(7);

           // jwt가 블랙리스트에 있는 경우 (로그아웃 해서 유효하지 않은 토큰)
           if(tokenBlacklistService.isBlacklisted(token)) {
               // 인증 실패 반환
               response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
               return;
           }
           // 토큰이 유효한지 검사
           if(jwtTokenProvider.validateToken(token)) {

               String username = jwtTokenProvider.getUsername(token);
               String role = jwtTokenProvider.getRole(token);

               // 권한 리스트. role이 하나여도 이런 식으로 전달
               List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

                // spring security에서 사용하는 인증 객체에 로그인 정보 저장
               UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                       // spring security의 User 객체. 비밀번호, role
                       new User(username, "", authorities), // 누구인지
                       null, authorities); // 인증 수단(jwt는 null), role

               // ip주소, 브라우저 정보 등등의 부가 정보도 같이 기록
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // 인증된 유저 등록. SecurityContextHolder는 현재 로그인된 유저 정보 저장하는 곳
                SecurityContextHolder.getContext().setAuthentication(authentication);
           }
        }
        // 다음 필터 || 컨트롤러로 요청을 넘김
        filterChain.doFilter(request,response);
    }
}
