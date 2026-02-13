# 📝 Todo List 
 
> 확장성을 고려해 Redis, Kafka, CI/CD까지 직접 적용한 실무형 REST API 서버

운영 환경을 가정하여 아래 요소를 직접 설계·구현했습니다.

- 서버 수평 확장을 고려한 JWT 기반 인증
- Redis 캐시를 통한 조회 성능 개선 및 DB 부하 경감
- Kafka 기반의 비동기 이벤트 처리
- AWS + Docker 기반 실제 배포 환경 구축
- GitHub Actions CI/CD 자동화 파이프라인
- Swagger 기반의 문서화 및 테스트 환경 구축
- 전역 예외처리 및 공통 로깅

---

## 📮 Swagger URL

http://ec2-54-180-166-227.ap-northeast-2.compute.amazonaws.com/swagger-ui/index.html

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
| CI/CD | GitHub Actions |
| **Build / Test** | Gradle, JUnit5, MockMvc |
| **Monitoring** | Spring Actuator, Prometheus, Grafana |
| Documentation | Swagger (OpenAPI) |

---

## 💡 설계 및 구현 포인트

### JWT 기반 무상태 인증
- 세션 의존 구조를 제거하여 수평 확장 가능한 인증 설계
- 트래픽 증가 상황에서도 병목이 발생하지 않도록 구성

### Redis 캐시를 통한 조회 성능 최적화
- 반복 조회 요청을 Redis 캐시로 처리하도록 설계
- 캐시 미스 시에만 DB를 조회하는 구조로 부하 분산

### Kafka 기반 비동기 이벤트 처리
- Todo 생성 후 부가 로직을 Kafka 이벤트로 분리
- REST API 응답 경로를 가볍게 유지하여 응답 속도 개선
- 향후 알림/확장 기능을 고려한 구조

### 관측성과 디버깅을 고려한 설계
- AOP 기반 요청·응답·에러 공통 로깅
- 전역 예외 처리로 일관된 에러 응답 제공

### 운영 환경을 고려한 배포 및 모니터링
- Docker 기반 컨테이너화로 실행 환경 일관성 확보
- Actuator + Prometheus + Grafana를 통한 지표 모니터링


---


## 📂 주요 코드 바로가기


| 영역 | 파일 / 경로 | 설명 |
|------|--------------|------|
| **인증 (JWT)** | [AuthService.java](src/main/java/com/example/todo/service/AuthService.java) | 회원가입 / 로그인 / 토큰 발급 |
| **보안 설정** | [SecurityConfig.java](src/main/java/com/example/todo/security/SecurityConfig.java) | Spring Security 설정 |
| **Todo 비즈니스 로직** | [TodoService.java](src/main/java/com/example/todo/service/TodoService.java) | CRUD, 캐싱, Kafka 이벤트 처리 |
| **Kafka 이벤트 발행** | [TodoProducer.java](src/main/java/com/example/todo/event/TodoProducer.java) | Todo 생성 시 Kafka 이벤트 발행 |
| **AOP 로깅** | [LoggingAspect.java](src/main/java/com/example/todo/aop/LoggingAspect.java) | 요청·응답 로깅 |
| **전역 예외 처리** | [CustomExceptionHandler.java](src/main/java/com/example/todo/exception/CustomExceptionHandler.java) | 예외 처리 및 응답 관리 |
| **테스트 코드** | [TodoControllerTest.java](src/test/java/com/example/todo/controller/TodoControllerTest.java) | 테스트 코드 |
