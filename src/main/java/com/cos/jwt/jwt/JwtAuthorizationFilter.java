package com.cos.jwt.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.cos.jwt.auth.PrincipalDetails;
import com.cos.jwt.model.User;
import com.cos.jwt.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


// 시쿠리티가 filter를 가지고 있는데 그 필터 중에 BasicAuthenticationFilter 라는 것이 있다.
// 권한이나 인증이 필요한 특정 주소를 요청했을 때 위 필터를 무조건 타게 되어있다.
// 만약에 권한이나 인증이 필요한 주소가 아니라면 이 필터를 안 탄다
@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private UserRepository userRepository;

    // constructor 생성 필
    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository) {
        super(authenticationManager);
        this.userRepository = userRepository;
    }


    // 인증이나 권한이 필요한 주소 요청이 있을 때 해당 필터를 타게 됨.
    //
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        //super.doFilterInternal(request, response, chain);
        System.out.println("인증이나 권한이 필요한 주소 요청이 됨");

        // 헤더 값을 확인하자
        String jwtHeader =  request.getHeader("Authorization");
        System.out.println("jwt Header:" + jwtHeader);


        // header 있는지 확인
        if (jwtHeader == null || !jwtHeader.startsWith("Bearer")) { //jwt 토큰이 null(없거나)이거나 Bearer 로 시작하는게 아니라면
                                                // Bearer 은 우리가 jwt 발급할때 일부러 붙였었던 그 문자 JwtAuthenticationFilter.java 참고
            chain.doFilter(request, response); // 다시 필터를 타게 넘겨버리고
            return;   // 강사 코드 그대로 따라 적고 있는데 어차피 chain 때문에 다른 쪽으로 넘어갈 건게 여기 return 왜 넣은 걸까??,,

        }

        // JWT 토큰을 검증해서 정상적인 사용자인지 확인
        String jwtToken = request.getHeader("Authorization").replace("Bearer ", ""); // 헤더에 있는 토큰의 "Bearer " 부분을 공백으로 치환

        String username =
                JWT.require(Algorithm.HMAC512("cos")).build().verify(jwtToken).getClaim("username").asString();
        // secret cos로 설정했었음 JwtAuthenticationFilter.java 에서
        // └▶ 서명하는 과정 서명에 성공하면 claim에서 username을 가지고 오겠다. 그리고 asString() 으로 캐스팅하겠다.

        // 서명이 정상적으로 됨 username이 null 이 아님
        if (username != null) {

            User userEntity = userRepository.findByUsername(username);

            PrincipalDetails principalDetails = new PrincipalDetails(userEntity);
            // jwt 토큰 서명을 통해 서명이 정상이면 Authentication 객체를 만들어준다.
            // 로그인 처리가 아니라 서명을 통해 authentication을 만드는 거임
            Authentication authentication =                                 // 현재 진짜 로그인 서비스가 아니라 passowrd를 null 한 것임
                    new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities()); //강제로 만드는 Authentication 객체

            // 강제로 시큐리티 세션에 접근하여 authentication 객체를 저장한 것!
            SecurityContextHolder.getContext().setAuthentication(authentication);

        }

        chain.doFilter(request,response);

    }
}
