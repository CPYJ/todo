package com.example.todo.controller;

import com.example.todo.dto.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// 스프링부트를 실행해서 테스트
@SpringBootTest
// 실제 서버 띄우지 않고 가짜 http 요청 응답 테스트 가능
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc; // 가짜 클라이언트

    @Autowired
    private ObjectMapper objectMapper; // json <-> 객체 변환 도구


    // 로그아웃 -> jwt 블랙리스트 등록 -> 401
    @Test
    void logout_blacklist_works() throws Exception {
        // 회원가입
        UserDto user = new UserDto("logoutUser", "logout1234");
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());

        // 로그인 → JWT 발급
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("ey"))) // JWT 형태 확인
                .andReturn();

        // jwt 저장
        String token = loginResult.getResponse().getContentAsString();


        // 로그아웃 -> 해당 jwt 블랙리스트에 등록
        mockMvc.perform(post("/api/auth/logout")
                    .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("로그아웃 완료")));

        // 같은 토큰으로 접근 -> 401 unauthorized 차단
        mockMvc.perform(get("/api/todos")
                    .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }



    @Test
    void signUp_success() throws Exception {

        UserDto user = new UserDto("testUser", "test1234");
        // 실제처럼 요청 보내기
        mockMvc.perform(post("/api/auth/signup")
                        // 요청 바디 json 타입
                        .contentType(MediaType.APPLICATION_JSON) 
                        // 객체 -> json 바꾸기
                        .content(objectMapper.writeValueAsString(user))) 
                .andExpect(status().isOk())
                .andExpect(content().string("회원가입 성공"));
    }



    @Test
    void signUp_fail_duplicate_username() throws Exception {
        UserDto user = new UserDto("test", "test1234");

        // 회원 가입
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());
        
        // 중복 회원가입
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)  // 요청 바디 json 타입
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("이미 존재하는 유저 입니다."));
    }


    @Test
    void signUp_fail_validation_blank_username() throws Exception {
        UserDto userWithoutUsername = new UserDto("", "test1234");

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)  // 요청 바디 json 타입
                        .content(objectMapper.writeValueAsString(userWithoutUsername)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.username").value("username은 필수로 입력해야 합니다."));
    }


    @Test
    void signUp_fail_validation_short_password() throws Exception {
        UserDto userWithShortPassword = new UserDto("test", "test");

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)  // 요청 바디 json 타입
                        .content(objectMapper.writeValueAsString(userWithShortPassword)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.password").value("비밀번호는 최소 8자 이상 입니다."));
    }



    @Test
    void logIn_success() throws Exception {
        UserDto user = new UserDto("loginUser", "loginTest123");

        // 회원 가입
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());
        

        // 로그인
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                // jwt 토큰이 ey로 보통 시작함
                .andExpect(content().string(Matchers.containsString(("ey"))));
    }


    @Test
    void logIn_fail_no_username() throws Exception {
        UserDto user = new UserDto("hello", "test1234");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("존재하지 않는 사용자"));
    }


    @Test
    void logIn_fail_wrong_password() throws Exception {
        UserDto correctPWUser = new UserDto("pwTest", "test1234");
        UserDto wrongPwUser = new UserDto("pwTest", "wrongPassword");

        // 회원 가입
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(correctPWUser)))
                .andExpect(status().isOk());

        // 로그인 시도
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wrongPwUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("비밀번호 불일치"));
    }
}
