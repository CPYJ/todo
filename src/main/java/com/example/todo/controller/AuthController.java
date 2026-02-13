package com.example.todo.controller;

import com.example.todo.dto.TokenResponse;
import com.example.todo.dto.UserDto;
import com.example.todo.entity.User;
import com.example.todo.repository.UserRepository;
import com.example.todo.response.ApiResponse;
import com.example.todo.security.JwtTokenProvider;
import com.example.todo.service.AuthService;
import com.example.todo.service.TokenBlacklistService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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


    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(@Valid @RequestBody UserDto userDto) {

        authService.signUp(userDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED, "signup success", null));
    }



    // 로그인 처리
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody UserDto userDto) {

        String token = authService.login(userDto);
        TokenResponse tokenResponse = new TokenResponse(token);

        return ResponseEntity.ok(ApiResponse.ok(tokenResponse));
    }


    // 로그아웃 시 jwt를 redis에 블랙리스트로 넣기
    @PostMapping("/logout")
    @SecurityRequirement(name = "bearerAuth") // swagger ui상에서 jwt 토큰이 필요한 메서드임을 표시
    public ResponseEntity<ApiResponse<Void>> logout(@RequestHeader("Authorization") String header) {

       authService.logout(header);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @Hidden // swagger api에서 제외
    @GetMapping("/test")
    @PreAuthorize("hasRole('USER')")
    public String test() {
        return "your're in!";
    }
}
