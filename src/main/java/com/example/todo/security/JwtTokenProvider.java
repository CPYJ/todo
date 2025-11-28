package com.example.todo.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

// JWT 토큰을 만들고 읽고 검사하는 클래스
@Component
public class JwtTokenProvider {

    // 객체 생성 뒤 주입되는 값이기 때문에 final, static 키워드를 쓰면 안된다
    @Value("${jwt.secret}")
    private String SECRET_KEY;
    private static final long EXPIRATION = 1000L * 60 * 60; // 1시간
    // secret key를 HMAC용 key 객체로 바꾸기 => 그래야만 암호화 가능
    private Key key;


    // 클래스가 빈으로 등록되고 나서 실행되는 메서드
    // postconstruct는 value, autowired 등의 주입이 다 끝난 뒤 실행
    @PostConstruct
    public void init() {
        this.key =  Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }


    // 토큰 생성
    public String generateToken(String username, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + EXPIRATION);

        return Jwts.builder() // jwt 만들 준비
                .setSubject(username) // 토큰 주인 설정
                .claim("role", role) // role 설정
                .setIssuedAt(now) // 토큰 발급 시간
                .setExpiration(expiry) // 토큰 유효 기간
                // HMAC + key로 (header + payload) 암호화 => signature 생성
                .signWith(key, SignatureAlgorithm.HS256) 
                .compact(); // JWT 문자열로 변환
    }
    
    
    // 토큰에서 username 추출
    public String getUsername(String token) {
        return Jwts.parserBuilder() // jwt를 파싱할 준비
                .setSigningKey(key) // 비밀키 넣기
                .build() // 파서 생성
                .parseClaimsJws(token) // 토큰 검증 > 데이터 꺼냄
                .getBody() // 데이터 (claim) 중 실제 내용 가져오기
                .getSubject(); // 토큰 주인(subject) 꺼내기
    }

    // 토큰에서 role 추출
    public String getRole(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class); // role 찾아서 꺼내기
    }
    
    
    // 토큰 만료시간 추출
    public long getExpiration(String token) {
        Date exp = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        
        return exp.getTime() - System.currentTimeMillis();
    }
    
    
    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token); // 토큰 검증에 실패하면 예외를 던짐
            return true;
            // 토큰 관련 예외 | 입력 자체가 문제 (토큰이 없는 경우)
        } catch(JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
