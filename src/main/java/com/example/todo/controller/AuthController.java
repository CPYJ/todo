package com.example.todo.controller;

import com.example.todo.dto.UserDto;
import com.example.todo.entity.User;
import com.example.todo.repository.UserRepository;
import com.example.todo.security.JwtTokenProvider;
import com.example.todo.service.AuthService;
import com.example.todo.service.TokenBlacklistService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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

    private final AuthService authService;

    // 회원가입. 응답 타입이 여러가지 일 수도 있어서 <?>
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody UserDto userDto) {

        authService.signUp(userDto);
        return ResponseEntity.ok("회원가입 성공");
    }



    // 로그인 처리
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserDto userDto) {

        String token = authService.login(userDto);
        return ResponseEntity.ok(token);
    }


    // 로그아웃 시 jwt를 redis에 블랙리스트로 넣기
    // swagger ui 상에서 jwt 토큰이 필요함을 표시하는 애노테이션
    @PostMapping("/logout")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String header) {

       authService.logout(header);
        return ResponseEntity.ok("로그아웃 완료");
    }

    @Hidden // swagger api에서 제외
    @GetMapping("/test")
    @PreAuthorize("hasRole('USER')")
    public String test() {
        return "your're in!";
    }
}
