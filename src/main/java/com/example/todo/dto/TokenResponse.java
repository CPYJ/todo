package com.example.todo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

// jwt 감싸기 용
@Getter
@AllArgsConstructor
public class TokenResponse {
    private String accessToken;
}
