# 📝 Todo List 
 
> CRUD, **JWT 인증**, **Redis 캐싱**, **Kafka 이벤트**,  
> **Docker + AWS ECR 배포**까지 직접 구현한 백엔드 프로젝트입니다.

---

## 🚀 기술 스택

| 구분 | 기술 |
|------|------|
| **Language** | Java 17 |
| **Framework** | Spring Boot 3.5.8, Spring Security, Spring Data JPA |
| **Database** | Postgres (AWS RDS) |
| **Cache** | Redis |
| **Message Queue** | Kafka |
| **Infra / DevOps** | Docker, Docker Compose, AWS EC2, AWS ECR |
| **Build / Test** | Gradle, JUnit5, MockMvc |
| **Monitoring** | Spring Actuator, Prometheus, Grafana |

---

## 💡 핵심 포인트

- **JWT + Spring Security**를 통한 안전한 사용자 인증
- **Redis 블랙리스트**로 로그아웃 시 토큰 무효화 및 재사용 차단  
- **Redis 캐싱**으로 조회 성능 향상 및 DB 부하 최소화  
- **Kafka 이벤트 발행** 기반의 확장 가능한 비동기 구조 설계  
- **AOP 로깅**으로 요청·응답·에러를 자동 추적하고 디버깅 효율 향상  
- **전역 예외 처리 (ControllerAdvice)** 로 일관성 있는 에러 응답 관리  
- **Actuator + Prometheus + Grafana** 로 서버 상태·요청 지표 실시간 모니터링  
- **Docker + Docker Compose + AWS(ECR·EC2·RDS)** 로 배포 환경 구축

---

## 📮 주요 API 예시 (Postman 테스트용)

### 🔸 회원가입
**POST** `ec2-54-180-166-227.ap-northeast-2.compute.amazonaws.com/api/auth/signup`
```json
{
  "username": "testuser",
  "password": "test1234"
}
````

**응답**

```text
회원가입 성공
```

---

### 🔸 로그인 (JWT 토큰 발급)

**POST** `ec2-54-180-166-227.ap-northeast-2.compute.amazonaws.com/api/auth/login`

```json
{
  "username": "testuser",
  "password": "test1234"
}
```

**응답 예시**

```text
eyJhbGciOiJIUzI1NiJ9...
```

> 이후 요청 시 헤더에 추가
> `Authorization: Bearer <token>`

---

### 🔸 Todo 등록

**POST** `ec2-54-180-166-227.ap-northeast-2.compute.amazonaws.com/api/todos`

> 헤더
> `Authorization: Bearer <token>`

```json
{
  "title": "Redis 캐싱 적용",
  "description": "성능 개선",
  "completed": false
}
```

---

### 🔸 Todo 전체 조회

**GET** `ec2-54-180-166-227.ap-northeast-2.compute.amazonaws.com/api/todos`

> 헤더
> `Authorization: Bearer <token>`

**응답 예시**

```json
[
  {
    "id": 1,
    "title": "Redis 캐싱 적용",
    "description": "성능 개선",
    "completed": false
  }
]
```

---

## 📂 주요 코드 바로가기


| 영역 | 파일 / 경로 | 설명 |
|------|--------------|------|
| **인증 (JWT)** | [AuthController.java](src/main/java/com/example/todo/controller/AuthController.java) | 회원가입 / 로그인 / 토큰 발급 |
| **보안 설정** | [SecurityConfig.java](src/main/java/com/example/todo/security/SecurityConfig.java) | Spring Security 설정 |
| **Todo 비즈니스 로직** | [TodoService.java](src/main/java/com/example/todo/service/TodoService.java) | CRUD, `@Cacheable`, `@CacheEvict`, Kafka 이벤트 처리 |
| **Kafka 이벤트 발행** | [TodoProducer.java](src/main/java/com/example/todo/event/TodoProducer.java) | Todo 생성 시 Kafka 이벤트 발행 |
| **AOP 로깅** | [LoggingAspect.java](src/main/java/com/example/todo/aop/LoggingAspect.java) | 요청·응답 로깅 |
| **전역 예외 처리** | [CustomExceptionHandler.java](src/main/java/com/example/todo/exception/CustomExceptionHandler.java) | 예외 처리 및 응답 관리 |

