package com.example.todo.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {

        // jwt 인증 방식을 swagger에 알려주기. 인증 방식 설명서
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP) // http 기반 인증
                .scheme("bearer") // bearer 토큰 방식
                .bearerFormat("JWT"); // 토큰 형식 JWT


        // OpenAPI 객체 생성
        OpenAPI openAPI = new OpenAPI();

        // API 문서 기본 정보 설정
        Info info = new Info()
                .title("TodoList API Server")
                .description("회원가입 / 로그인 / Todo API")
                .version("1.0.0");
        openAPI.info(info);


        // swagger 설정 보관함. security scheme 같은 인증설정들을 이름 붙여 저장
        Components components = new Components()
                .addSecuritySchemes("bearerAuth", securityScheme);
        openAPI.components(components);


        return openAPI;
    }


}
