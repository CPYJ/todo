package com.example.todo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    @NotBlank(message = "username은 필수로 입력해야 합니다.")
    @Size(max = 10, message = "id는 최대 10글자 입니다.")
    private String username;

    @NotBlank
    @Size(min = 8, message = "비밀번호는 최소 8자 이상 입니다.")
    private String password;
}
