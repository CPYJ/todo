package com.example.todo.aop;


import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j // log 객체를 자동으로 만들어줌
@Aspect // aop 클래스
@Component
public class LoggingAspect {
    

    // 모든 컨트롤러의 모든 메서드 실행 전에 적용
    @Before("execution(* com.example.todo.controller..*(..))")
    public void logBeforeMethod(JoinPoint joinpoint) {
        // 실행될 클래스 + 메서드 이름
        String methodName = joinpoint.getSignature().toShortString();
        // 매개변수
        String args = Arrays.toString(joinpoint.getArgs());

        // 로그 찍기
        log.info("🚀 요청 시작 : {} | 매개변수 : {} ", methodName, args);
    }

    // 성공 로그
    // returning에 있는 string과 파라미터의 변수 이름이 같아야 함
    @AfterReturning(pointcut="execution(* com.example.todo.controller..*(..))",
            returning = "result")
    public void logAfterMethod(JoinPoint joinpoint, Object result) {
        String methodName = joinpoint.getSignature().toShortString();

        log.info("✅ 요청 성공 : {} | 결과 : {} ", methodName, result);
    }



    // 예외 로그
    @AfterThrowing(pointcut="execution(* com.example.todo.controller..*(..))",
            throwing = "ex")
    public void logAfterException(JoinPoint joinpoint, Throwable ex) {
        String methodName = joinpoint.getSignature().toShortString();

        log.error("❌ 요청 실패 : {} | 예외 : {} ", methodName, ex.getMessage());
    }
}
