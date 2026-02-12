package com.example.todo.exception;

import org.springframework.http.HttpStatus;

public class InvalidPasswordException extends BaseException{

    public InvalidPasswordException() {
        super("비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED);
    }
}
