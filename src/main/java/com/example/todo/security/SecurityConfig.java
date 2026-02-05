package com.example.todo.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// 요청을 어떻게 보호할지 Spring Security를 설정하는 클래스
@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    // 보안 규칙 설정 메서드
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http    // 사이트 위조 공격 방지 기능 끄기. jwt는 불필요함
                .csrf(csrf -> csrf.disable())
                // 세션을 사용하지 않게 설정. jwt는 세션에 유저 정보 저장 안함
                .sessionManagement(sess ->
                        sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // url 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 인증이 필요없는 경로
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/actuator/prometheus").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        // 그 외에는 전부 인증된 사용자만 접근 가능
                        .anyRequest().authenticated()
                )
                // 커스텀 필더를 기본 로그인 필터보다 먼저 실행되게 등록
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        // 설정 완료 후 SecurityFilterChain 생성하여 반환
        return http.build();
    }



    // 암호화 도구 등록
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
}
