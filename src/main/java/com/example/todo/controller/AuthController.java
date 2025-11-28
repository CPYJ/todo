package com.example.todo.controller;

import com.example.todo.dto.UserDto;
import com.example.todo.entity.User;
import com.example.todo.repository.UserRepository;
import com.example.todo.security.JwtTokenProvider;
import com.example.todo.service.TokenBlacklistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Validated
public class AuthController {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder passwordEncoder;
    private final TokenBlacklistService tokenBlacklistService;


    // 회원가입. 응답 타입이 여러가지 일 수도 있어서 <?>
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody UserDto userDto) {
        String username = userDto.getUsername();
        // 비밀번호 암호화
        String password = passwordEncoder.encode(userDto.getPassword());

        // 이미 같은 username이 존재하는지 확인
        if(userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("이미 존재하는 유저 입니다.");
        }

        userRepository.save(User.builder()
                .username(username)
                .password(password)
                .role("ROLE_USER")
                .build());

        return ResponseEntity.ok("회원가입 성공");
    }



    // 로그인 처리
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserDto userDto) {
        String username = userDto.getUsername();
        String password = userDto.getPassword();

        // 로그인한 사용자가 없는 경우
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자"));

        // 로그인한 사용자는 있지만 비번이 다른 경우
        if(!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("비밀번호 불일치");
        }

        // 로그인 성공한 경우 jwt 토큰 반환. payload => username, role
        String token = jwtTokenProvider.generateToken(username, user.getRole());

        return ResponseEntity.ok(token);
    }


    // 로그아웃 시 jwt를 redis에 블랙리스트로 넣기
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String header) {
        // jwt 가져오기
        String token = header.replace("Bearer ", "");
        long ttl = jwtTokenProvider.getExpiration(token);

        tokenBlacklistService.blacklist(token,ttl);

        return ResponseEntity.ok("로그아웃 완료");
    }

    @GetMapping("/test")
    @PreAuthorize("hasRole('USER')")
    public String test() {
        return "your're in!";
    }
}
