package com.example.todo.response;

import lombok.Builder;
import lombok.Getter;
import lombok.val;
import org.springframework.http.HttpStatus;

@Getter
@Builder
public class ApiResponse<T> {

    private final int status; // http 상태 코드
    private final String message;
    private final T data; // 실제 데이터


    // 객체 생성(자기자신) 도우미이기 때문에 static
    // 이 메서드가 사용할 제네릭 타입 선언. 파라미터로 추론
    
    // status code = ok, message = ok, data = custom 형태
    public static <T> ApiResponse<T> ok(T data) {
        return ApiResponse.<T>builder()
                .status(HttpStatus.OK.value())
                .message("ok")
                .data(data)
                .build();
    }


    // 특정 상태코드 입력 용
    public static <T> ApiResponse<T> success(HttpStatus status,String msg, T data) {
        return ApiResponse.<T>builder()
                .status(status.value())
                .message(msg)
                .data(data)
                .build();
    }


    // data 없이 msg만 있는 예외 응답용
    public static ApiResponse<Void> error(HttpStatus status, String message) {
        return ApiResponse.<Void>builder()
                .status(status.value())
                .message(message)
                .data(null)
                .build();

    }


    // data 포함 예외 응답용 (validation 용)
    public static <T> ApiResponse<T> error(HttpStatus status, String msg, T data) {

        return ApiResponse.<T>builder()
                .status(status.value())
                .message(msg)
                .data(data)
                .build();
    }
}
