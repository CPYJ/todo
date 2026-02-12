package com.example.todo.exception;

import org.springframework.http.HttpStatus;

// 찾는 Todo가 없을 때 발생하는 예외
public class TodoNotFoundException extends BaseException{

    public TodoNotFoundException() {
        // super(msg, status code)
        super("Todo를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
    }
}
