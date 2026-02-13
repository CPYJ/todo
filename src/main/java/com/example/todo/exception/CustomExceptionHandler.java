package com.example.todo.exception;

import com.example.todo.response.ApiResponse;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

// 모든 컨트롤러의 예외를 한번에 잡아서 처리하는 곳
@RestControllerAdvice 
public class CustomExceptionHandler {


    // custom exception 예외 처리
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponse<?>> handleBaseException(BaseException ex){
        // api response로 감싸서 응답
        return ResponseEntity.status(ex.getStatus())
                .body(ApiResponse.error(ex.getStatus(), ex.getMessage()));
    }




    // validation 실패 시 발생하는 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(
            MethodArgumentNotValidException ex) {

        Map<String,String> errors = new HashMap<>();

        // validation에 실패한 필드들을 하나씩 꺼내서 errors에 넣기
        ex.getBindingResult().getAllErrors().forEach(error-> {
            String fieldName = ((FieldError)error).getField();
            // 애노테이션에 적어둔 validation 메시지 꺼내기
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        // 400 상태코드와 errors 응답
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(HttpStatus.BAD_REQUEST,
                        "Validation failed", errors));
    }


    

}
