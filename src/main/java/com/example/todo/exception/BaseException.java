package com.example.todo.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

// 앞으로 만들 Custom Exception들을 다른 runtimeException들과 분리하기 위해
// base가 될 부모 exception 생성
@Getter
public class BaseException extends RuntimeException {

    // http 상태 코드. RuntimeException 에는 없음
    private final HttpStatus status;

    public BaseException(String message, HttpStatus status) {
        // 부모인 runtimeException이 msg를 받고, getMessage 기능을 갖고 있음
        // 부모 객체 먼저 생성 -> 자식 객체 생성
        super(message);
        this.status = status;
    }
}
