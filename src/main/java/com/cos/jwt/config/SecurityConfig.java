package com.cos.jwt.config;

import com.cos.jwt.filter.MyFilter1;
import com.cos.jwt.filter.MyFilter3;
import com.cos.jwt.jwt.JwtAuthenticationFilter;
import com.cos.jwt.jwt.JwtAuthorizationFilter;
import com.cos.jwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CorsFilter corsFilter;
    private final UserRepository userRepository;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // MyFilter3가 시큐리티 필터보다 먼저 동작하게 설정
        //http.addFilterBefore(new MyFilter3(), SecurityContextPersistenceFilter.class);
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션을 사용하지 않겠다는 의미
                // 우리는 token을 사용할 예정이니깐
                .and()
                .addFilter(corsFilter) // 이렇게 하면 모든 요청이 이 안의 필터를 탄다.
                // 필터 적용 방법 중 컨트롤러에 붙이는 @CrossOrigin 어노테이션과 다른 점은
                // @CrossOrigin(인증 없을 때는), 시쿠리티 필터에 등록 (인증이 있을 때는)
                .formLogin().disable()   // jwt 서버라서 아이디 비밀번호를 폼 로그인하지 않는다. -> 폼 형태의 로그인을 사용하지 않는다.
                .httpBasic().disable()
                .addFilter(new JwtAuthenticationFilter(authenticationManager())) // AuthenticationManager를 던져줘야 한다 파라미터로
                .addFilter(new JwtAuthorizationFilter(authenticationManager(), userRepository)) // 인증이나 권한 요구시 자동 동작할 필터
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