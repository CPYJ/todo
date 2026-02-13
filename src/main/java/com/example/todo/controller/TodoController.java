package com.example.todo.controller;

import com.example.todo.dto.TodoDto;
import com.example.todo.response.ApiResponse;
import com.example.todo.service.TodoService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid; // Bean Validation 작동 시킴
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
@Validated // 검증 기능 사용하기
@SecurityRequirement(name = "bearerAuth") // swagger 에서 jwt가 필요함을 표시 (자물쇠 ui 넣기)
public class TodoController {

    private final TodoService todoService;



    @GetMapping
    public ResponseEntity<ApiResponse<List<TodoDto>>> getAllTodos() {

        return ResponseEntity.ok(ApiResponse.ok(todoService.getAllTodos()));
    }



    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TodoDto>> getTodoById(@PathVariable Long id) {
        // 응답 데이터와 상태 코드를 한 번에 담아보낼 수 있음
        return ResponseEntity.ok(ApiResponse.ok(todoService.getTodoById(id)));
    }



    @PostMapping
    public ResponseEntity<ApiResponse<TodoDto>> createTodo(@Valid @RequestBody TodoDto dto) {

        TodoDto created = todoService.createTodo(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED, "created", created));
    }



    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TodoDto>> updateTodo(@PathVariable Long id,
                                              // 실제 검증
                                              @Valid @RequestBody TodoDto dto) {
        return ResponseEntity.ok(ApiResponse.ok(todoService.updateTodo(id, dto)));
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTodo(@PathVariable Long id) {
        todoService.deleteTodo(id);
        // 상태 코드 204 = 요청 성공 + 응답 바디 없음
        return ResponseEntity.noContent().build();
    }
}
