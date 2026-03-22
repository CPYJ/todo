package com.example.todo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    // sequence가 identity보다 postgres에선 유리함 <- db 왕복 적게 함
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String eventType; // 어떤 이벤트?

    private Long userId; // 누가

    private String data; // 전체 데이터 (json)

    private LocalDateTime createdAt; // 언제
}
