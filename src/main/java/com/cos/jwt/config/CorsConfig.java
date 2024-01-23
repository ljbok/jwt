package com.cos.jwt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

// 스프링에서 관리할 수 있도록 @Configuration 어노테이션을 붙여준다.
@Configuration
public class CorsConfig {


    // 이렇게 걸기만 하면 소용없고 이렇게 만든 메소드를 SecurityConfig.java 에 필터로 지정해놔야 한다.
    // 스프링이 이 함수조차조 직접 관리할 수 있도록 bean 어노테이션 붙이기
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); // 내 서버가 응답을 할 때 json을 자바스크립트에서 처리할 수 있게 할지를 설정하는 것
                                // fetch ajax axios 같은 라이브러리로 데이터를 요청하면 그 응답을 자바스크립트가 받을 수 있게 할 것인지
                                 // 여기 false가 걸려있다면. 자바스크립트로 어떤 요청을 했을 때 응답이 오지 않는다.
        config.addAllowedOrigin("*"); // 모든 ip에 응답을 허용하겠다.
        config.addAllowedHeader("*"); // 모든 header에 응답을 허용하겠다.
        config.addAllowedMethod("*"); // 모든 post, get, put, delete, patch 요청을 허용하겠다.
        source.registerCorsConfiguration("/api/**", config);
        return new CorsFilter(source);
    }
}
