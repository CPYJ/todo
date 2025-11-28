package com.example.todo.event;

import com.example.todo.dto.TodoDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Slf4j
@Component
// kafka에 등록된 event를 받아서, 이후 해야할 일들을 비동기로 처리하는 역할
// => 메인 로직 빨리 끝남
public class TodoConsumer {

    private final ObjectMapper objectMapper = new ObjectMapper();


    // kafka에 메시지가 들어오면 자동으로 실행 됨 <= kafkaListener
    // topics : 어떤 topic의 메시지를 읽어올지
    // groupId : 같은 그룹끼리 메시지 분담 처리 가능
    @KafkaListener(topics = "${spring.kafka.topic.todo-created}", groupId = "todo-group")
    public void handleTodoCreated(String message) {

        try {
            // 받은 json -> 객체
            TodoDto dto = objectMapper.readValue(message, TodoDto.class);
            log.info("📥 Kafka 메시지 수신: {}", dto);
            // 추후에 해야할 일들 생기면 여기에 추가하기만 하면 끝
            // ex) 알림, 메일 보내기 등등

        } catch (Exception e) {

            // 이벤트 처리 담당이라 실패해도 메인 로직에 영향 없음 
            // kafka가 기본적으로 재시도/보류 처리도 지원함
            log.error("❌ Kafka 메시지 처리 실패: {}", message, e);
        }
    }
}
