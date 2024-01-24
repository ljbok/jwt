package com.cos.jwt.config;

import com.cos.jwt.filter.MyFilter1;
import com.cos.jwt.filter.MyFilter2;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // ioc로 등록
public class FilterConfig {

    // 여기다가 커스텀 제네릭 디렉션인 MyFilter1은 filter/ 경로에서 내가 임의로 생성한 filter
    @Bean
    public FilterRegistrationBean<MyFilter1> filter1(){
        FilterRegistrationBean<MyFilter1> bean = new FilterRegistrationBean<>(new MyFilter1());
        bean.addUrlPatterns("/*"); // 모든 요청에서 다 해라
        bean.setOrder(0);// 순서를 정할 수 있는데 낮을 수록 필터의 우선순위가 높음
                        // 즉 0 == 최우선순쉬로 지정임
        return bean; //--> 이러면 자동으로 필터 등록이 된 거임 실행될 거임
    }

    @Bean
    public FilterRegistrationBean<MyFilter2> filter2(){
        FilterRegistrationBean<MyFilter2> bean = new FilterRegistrationBean<>(new MyFilter2());
        bean.addUrlPatterns("/*"); // 모든 요청에서 다 해라
        bean.setOrder(1);// 순서를 정할 수 있는데 낮을 수록 필터의 우선순위가 높음
                        // 즉 1 == 최우선순위인 0 필터 다음으로 실행될 필터로 지정임
        return bean; //--> 이러면 자동으로 필터 등록이 된 거임 실행될 거임
    }
}
