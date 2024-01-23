package com.cos.jwt.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션을 사용하지 않겠다는 의미
                                                                                // 우리는 token을 사용할 예정이니깐
        .and()
        .formLogin().disable()   // jwt 서버라서 아이디 비밀번호를 폼 로그인하지 않는다. -> 폼 형태의 로그인을 사용하지 않는다.
        .httpBasic().disable()
        .authorizeRequests()
        .antMatchers("/api/v1/user/**") // 이 url에 접속하려면 바로 아래 줄의 권한이 있어야 한다.
        .access("hasRole('ROLE_USER') or hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
        .antMatchers("/api/v1/manager/**") // 이 url에 접속하려면 바로 아래 줄의 권한이 있어야 한다.
        .access("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
        .antMatchers("/api/v1/admin/**") // 이 url에 접속하려면 바로 아래 줄의 권한이 있어야 한다.
        .access("hasRole('ROLE_ADMIN')")
        .anyRequest().permitAll(); // 이외의 다른 요청은 권한 없이 접속 가능하게

        // ---위 access와 antMathers는 사용 의도와 맞게 그때마다 커스텀해서 생성하면 된다. 여기까지가 jwt 사용을 위한 기본 세팅이다.




    }
}
