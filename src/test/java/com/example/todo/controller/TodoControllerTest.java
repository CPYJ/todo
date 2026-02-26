package com.example.todo.controller;

import com.example.todo.dto.TodoDto;
import com.example.todo.dto.UserDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(OutputCaptureExtension.class) // 로그 캡쳐 기능
@SpringBootTest
@AutoConfigureMockMvc // MockMvc를 자동으로 만들어주는 설정
public class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc; // 가짜 브라우저

    @Autowired
    private ObjectMapper objectMapper; // Json <-> 객체 변환기

    // 로그인 후 받은 jwt 저장용
    private String token;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;




    // 매 테스트 전마다 실행될 메서드. 테스트 메서드 아니고 준비용 메서드
    @BeforeEach
    void setUp_getToken() throws Exception{

        UserDto user = new UserDto("testUser", "test1234");

        // 회원 가입
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)));
        // 굳이 duplicate 예외 터지나 검증 안함.

        // 로그인 -> jwt 토큰 발급
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(user)))
                                    .andExpect(status().isOk())
                                    .andReturn(); // MvcResult 반환
        // 발급받은 jwt 저장
        String body = result.getResponse().getContentAsString();
        //                   json 탐색기
        token = objectMapper.readTree(body).get("data").get("accessToken").asText();
    }




    // redis 캐시가 실제로 잘 작동하는지 테스트
    @Test
    void getAllTodos_redis_cache_works(CapturedOutput output) throws Exception {

        // 캐시 초기화
        redisTemplate.getConnectionFactory().getConnection().flushAll();

        // 첫 select 요청이라 db에서 조회 -> 로그 출력
        mockMvc.perform(get("/api/todos")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        // 두번 째 요청이라 캐시에서 응답 -> 로그 없음
        mockMvc.perform(get("/api/todos")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());


        // 로그가 딱 한 번만 찍혔는지 확인
        long cnt = output.getOut().lines()
                .filter(line -> line.contains("DB에서 Todos 조회 중"))
                .count();

        Assertions.assertThat(cnt).isEqualTo(1);
    }




    // 토큰 없이 요청 시 접근 거부 되는지 확인
    @Test
    void getAllTodos_fail_noToken() throws Exception {
        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isForbidden());
    }


    @Test
    void createTodo_success() throws Exception{
        TodoDto dto = TodoDto.builder()
                .title("청소")
                .description("청소기 돌리기")
                .completed(false)
                .build();

        mockMvc.perform(post("/api/todos")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.title").value("청소"));
    }



    @Test
    void createTodo_fail_validation_noTitle() throws Exception {
        TodoDto dto = TodoDto.builder()
                .title("")
                .description("test")
                .completed(false)
                .build();

        mockMvc.perform(post("/api/todos")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.title").value("제목은 비워둘 수 없습니다."));
    }


    @Test
    void getAllTodos_success() throws Exception {
        mockMvc.perform(get("/api/todos")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }



    // 존재하지 않는 id 조회
    @Test
    void getTodoById_fail_notFound() throws Exception {
        int id = 999;

        mockMvc.perform(get("/api/todos/"+ id)
                    .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Todo를 찾을 수 없습니다."));
    }





    @Test
    void updateTodo_success() throws Exception {
        TodoDto dto = TodoDto.builder()
                .title("initial title")
                .description("initial description")
                .completed(false)
                .build();

        MvcResult result = mockMvc.perform(post("/api/todos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();

        // id 가져오기
        String response = result.getResponse().getContentAsString();
        Long id = objectMapper.readTree(response).get("data").get("id").asLong();


        // 수정 요청
        TodoDto updateDto = TodoDto.builder()
                .title("updated title")
                .description("updated description")
                .completed(true)
                .build();

        mockMvc.perform(put("/api/todos/" + id)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("updated title"))
                .andExpect(jsonPath("$.data.completed").value(true));

    }



    @Test
    void deleteTodo_success() throws Exception {
        TodoDto dto = TodoDto.builder()
                .title("delete")
                .description("will be deleted")
                .completed(false)
                .build();

        MvcResult result = mockMvc.perform(post("/api/todos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();

        Long id = objectMapper.readTree(result.getResponse().getContentAsString()).get("data").get("id").asLong();

        mockMvc.perform(delete("/api/todos/" + id)
                    .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }



    @Test
    void deleteTodo_fail_notFound() throws Exception {
        mockMvc.perform(delete("/api/todos/9999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Todo를 찾을 수 없습니다."));
    }


}
