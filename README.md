# 📝 Todo List 
 
> Todo List REST API 프로젝트입니다.  
> **트래픽 증가 상황을 가정하고**
> - 서버 수평 확장을 고려한 JWT 기반 인증
> - Redis 캐시를 통한 조회 성능 개선 및 DB 부하 경감  
> - Kafka 기반의 비동기 이벤트 처리
> - AWS + Docker 환경에서의 배포 및 모니터링  
> 을 목표로 설계·구현했습니다.

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

## 💡 설계 및 구현 포인트

- **JWT 기반 무상태 인증**
  - 서버 세션을 사용하지 않아 수평 확장에 유리한 구조
  - 인증 로직이 트래픽 병목이 되지 않도록 설계

- **Redis 캐시를 통한 조회 성능 최적화**
  - 조회 요청 증가 시 DB 병목을 줄이기 위해 캐시 도입
  - 캐시 미스 시에만 DB 조회하는 구조로 DB 부하 분산·응답속도 향상

- **Kafka 기반 비동기 이벤트 처리**
  - Todo 생성 후 처리 로직을 이벤트로 분리
  - 요청 경로를 가볍게 유지하여 응답 속도 개선
  - 향후 알림 등의 기능 확장 고려

- **관측성과 디버깅을 고려한 설계**
  - AOP 기반 요청·응답·에러 로깅
  - 전역 예외 처리로 일관된 에러 응답 제공

- **운영 환경을 고려한 배포 및 모니터링**
  - Docker를 활용하여 환경의 일관성 확보
  - Actuator + Prometheus + Grafana로 서버 지표 시각화


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
| **Todo 비즈니스 로직** | [TodoService.java](src/main/java/com/example/todo/service/TodoService.java) | CRUD, 캐싱, Kafka 이벤트 처리 |
| **Kafka 이벤트 발행** | [TodoProducer.java](src/main/java/com/example/todo/event/TodoProducer.java) | Todo 생성 시 Kafka 이벤트 발행 |
| **AOP 로깅** | [LoggingAspect.java](src/main/java/com/example/todo/aop/LoggingAspect.java) | 요청·응답 로깅 |
| **전역 예외 처리** | [CustomExceptionHandler.java](src/main/java/com/example/todo/exception/CustomExceptionHandler.java) | 예외 처리 및 응답 관리 |
| **테스트 코드** | [TodoControllerTest.java](src/test/java/com/example/todo/controller/TodoControllerTest.java) | 테스트 코드 |
