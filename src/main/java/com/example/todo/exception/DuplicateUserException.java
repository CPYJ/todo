package com.example.todo.exception;

import org.springframework.http.HttpStatus;

public class DuplicateUserException extends BaseException {

    public DuplicateUserException(){
        super("이미 존재하는 사용자 입니다.", HttpStatus.CONFLICT);
    }
}
