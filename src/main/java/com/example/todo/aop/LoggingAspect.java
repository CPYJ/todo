package com.example.todo.aop;


import java.util.Arrays;
import java.util.UUID;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j // log 객체를 자동으로 만들어줌
@Aspect // aop 클래스
@Component
public class LoggingAspect {

    // aop를 어디에 적용할지 매번 적지 않기 위해
    // 따로 정의해놓는 것. 별칭용
    // 모든 메서드 실행 전에 적용
    @Pointcut("execution(* com.example.todo.controller..*(..))")
    private void controllerPointcut(){}
    
    // 요청별 시작시간 저장 (응답 시간 계산용)
    private final ThreadLocal<Long> startTime = new ThreadLocal<>();



    @Before("controllerPointcut()")
    public void logBeforeMethod(JoinPoint joinpoint) {

        // 흐름 추적을 용이하게 하기 위해 request id 생성
        String requestId = UUID.randomUUID().toString().substring(0,8);
        // MDC = 로그용 개인 메모장. 한 요청동안 유지됨
        MDC.put("requestId", requestId);

        // 요청 시작 시간 저장
        startTime.set(System.currentTimeMillis());

        // 실행될 클래스 + 메서드 이름
        String methodName = joinpoint.getSignature().toShortString();
        // 매개변수
        String args = Arrays.toString(joinpoint.getArgs());

        // 로그 찍기
        log.info("🚀 [{}] 요청 시작 : {} | 매개변수 : {} ",requestId, methodName, args);
    }



    // 성공 로그
    // returning에 있는 string과 파라미터의 변수 이름이 같아야 함 => 주입
    @AfterReturning(pointcut="controllerPointcut()",
            returning = "result")
    public void logAfterMethod(JoinPoint joinpoint, Object result) {
        String methodName = joinpoint.getSignature().toShortString();

        long time = System.currentTimeMillis() - startTime.get();
        // MDC에서 request id 가져오기
        String requestId = MDC.get("requestId");

        log.info("✅ [{}] 요청 성공 : {} | time = {}ms | 결과 : {} ", requestId, methodName, time, result);

        // 다른 요청에서 스레드 재사용 시, 기존 값이 튀어나오지 않게 값을 비워주기 위함
        startTime.remove();
        MDC.clear();
    }



    // 예외 로그
    @AfterThrowing(pointcut="controllerPointcut()",
            throwing = "ex")
    public void logAfterException(JoinPoint joinpoint, Throwable ex) {
        String methodName = joinpoint.getSignature().toShortString();

        long time = System.currentTimeMillis() - startTime.get();
        String requestId = MDC.get("requestId");

        log.error("❌ [{}] 요청 실패 : {} | time = {}ms | 예외 : {} ", requestId, methodName, time, ex.getMessage());

        startTime.remove();
        MDC.clear();
    }
}
