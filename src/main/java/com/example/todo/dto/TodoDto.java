package com.example.todo.dto;


import com.example.todo.entity.Todo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TodoDto implements Serializable { // redis에 저장하기 위해

    //요청에서는 사용자가 보내도 엔티티에서 무시되고, 응답으로는 알려줘야 함
    // nullable
    private Long id;
    
    @NotBlank(message = "제목은 비워둘 수 없습니다.")
    @Size(max = 30, message= "제목은 최대 30자 입니다.")
    private String title;
    
    @Size(max=200, message = "설명은 최대 200자 입니다.")
    private String description;

    // 기본값은 false
    private boolean completed;

    // 클라이언트가 보내는 것 X. 서버에서 채우는 응답용 필드
    // 따라서 validation 안해도 됨
    private Long userId;



    // Entity를 받아서 Dto로 바꾼 다음 반환하는 메서드
    public static TodoDto changeEntityToDto(Todo todo) {
        return TodoDto.builder()
                .id(todo.getId())
                .title(todo.getTitle())
                .description(todo.getDescription())
                .completed(todo.isCompleted())
                .userId(todo.getUser().getId())
                .build();
    }
}

