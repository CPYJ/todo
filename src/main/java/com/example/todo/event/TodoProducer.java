package com.example.todo.event;

import com.example.todo.dto.TodoDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service // 비즈니스 로직 일부이기 때문에 service 클래스로 등록
@RequiredArgsConstructor
@Slf4j
// Kafka로 메시지 (이벤트) 를 보내는 역할 담당
// Consumer 쪽에서 받아서 이후 해야할 일들을 메인로직과 별개로 실행
public class TodoProducer {

    // kafka로 메시지 보내는 도구
    private final KafkaTemplate<String,String> kafkaTemplate;
    private final ObjectMapper objectMapper; // 자바 객체 -> json

    @Value("${spring.kafka.topic.todo-created}") // application.yml 값 주입
    private String todoCreatedTopic;



    // 생성 이벤트를 kafka에 전송하는 메서드
    public void sendTodoCreated(TodoDto dto) {

        try {
            // data -> json
            String data = objectMapper.writeValueAsString(dto);
            // 실제로 kafka의 topic에 메시지 보내기
            kafkaTemplate.send(todoCreatedTopic, data);
            log.info("📤 Kafka 전송 성공: {}", data);

        } catch (Exception e) {

            log.error("❌ Kafka 전송 실패", e);
            // 서비스 로직의 일부이기 때문에 메시지 못보내면 비즈니스 실패로 봐서 런타임 예외 던짐
            throw new RuntimeException("Kafka 메시지 전송 중 오류 발생", e);
        }

    }
}
