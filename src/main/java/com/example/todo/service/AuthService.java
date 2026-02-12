package com.example.todo.service;

import com.example.todo.dto.UserDto;
import com.example.todo.entity.User;
import com.example.todo.repository.UserRepository;
import com.example.todo.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder passwordEncoder;
    private final TokenBlacklistService tokenBlacklistService;


    public void signUp(UserDto userDto){
        String username = userDto.getUsername();
        // 이미 같은 username이 존재하는지 확인
        if(userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("이미 존재하는 유저 입니다.");
        }

        // 비밀번호 암호화
        String password = passwordEncoder.encode(userDto.getPassword());

        userRepository.save(User.builder()
                .username(username)
                .password(password)
                .role("ROLE_USER")
                .build());
    }


    @Transactional(readOnly = true)
    public String login(UserDto userDto){
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

        return token;
    }



    @Transactional(readOnly = true)
    public void logout(String header) {
        // jwt 가져오기
        String token = header.replace("Bearer ", "");
        long ttl = jwtTokenProvider.getExpiration(token);

        tokenBlacklistService.blacklist(token,ttl);
    }
}
