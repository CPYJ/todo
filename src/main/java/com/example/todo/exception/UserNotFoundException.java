package com.example.todo.exception;

import org.springframework.http.HttpStatus;

// 찾는 사용자가 존재하지 않을 때 터지는 예외
public class UserNotFoundException extends BaseException{

    public UserNotFoundException(){
        super("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
    }
}
