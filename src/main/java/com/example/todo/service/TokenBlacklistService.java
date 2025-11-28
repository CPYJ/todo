package com.example.todo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

// 로그아웃 된 토큰은 블랙리스트에 넣고 들어오지 못하게 하기 위함
@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final RedisTemplate<String, String> redisTemplate;


    // logout시 blacklist 토큰으로 저장
    public void blacklist(String token, long ttl) {
        redisTemplate.opsForValue()
                // 토큰 이름, 값, 데이터 만료 시간 설정
                .set("BL:" + token, "logout", ttl, TimeUnit.MILLISECONDS);
    }

    // 토큰이 블랙리스트에 있는지 확인
    public boolean isBlacklisted(String token) {
        return redisTemplate.hasKey("BL:" + token);
    }

}
