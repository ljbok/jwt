package com.cos.jwt.jwt;

import com.cos.jwt.auth.PrincipalDetails;
import com.cos.jwt.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// 스프링 시큐리티에서 UsernamePasswordAuthenticationFilter 이 필터가 있는데
// 원래 이 필터는 /login 요청해서 username과 password를 전송하면(post)
// 이 필터가 동작한다.
// 근데 지금 동작하지 안했던 이유는
// spring security config에서 formLogin.disable() 해뒀기 때문
// 그렇다면 어떻게 하면 작동시킬 수 있을까?
// JwtAuthenticationFilter 이 필터를 사디
// SecruityConfig에 필터로 등록해주면 된다.

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    // 로그인을 진행하는 필터
    private final AuthenticationManager authenticationManager;

    // 로그인을 수행하는 함수
    // login 요청을 하면 로그인 시도를 위해 실행되는 함수
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        System.out.println("JwtAuthenticationFilter:로그인 시도중");

        //id, pw를 확인해서 db로 확인해서 이 id와 pw가 정상이면 ▼
        // 1. username, password 를 받아서

        try{
            /*
            // x-www-formurlencoded와 json 읽어와서 콘솔 찍어본 방법
            BufferedReader br = request.getReader();
            String input = null;
            while ((input = br.readLine()) != null){
                System.out.println(input);
            */

            ObjectMapper om = new ObjectMapper(); // view로부터 넘어온 json을 파싱해주는 객체
            User user = om.readValue(request.getInputStream(), User.class);
            // ┗▶ 이렇게 하면 알아서 json 읽어온 값을 User 객체로 만들어준다.
            System.out.println(user);

            // 포스트맨으로 테스트하기 때문에 (폼로그인이 아니라서) 직접 임의 토큰 만들기
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()); // 첫번째 파마미터 username 두번째 파라미터 password
            // 이렇게 임의로 만든 토큰으로 로그인 시도를 한 번 해보자!
            // 토큰을 날리자 -> PrincipalDetailsService의 loadUserByUsername() 함수가 실행됨(username만 던짐 pw db에서 알아서 해줌 스프링이)
            // PrincipalDetailsService의 loadUserByUsername()가 실행된 후 정상이면 authenctication이 리턴된다.
            // 리턴되었다면 -> DB에 있는 username과 password가 일치한다는 뜻
            // DB에 있는 username과 password가 일치한다.
            Authentication authentication =    // 내가 로그인한 정보가 담긴다.
                    authenticationManager.authenticate(authenticationToken);
            // 제대로 로그인 되었다면 authentication 객체가 session 영역에 저장될 것이다! 그리고 꺼내봤을 때 null 이 아닐 것이다!
            // 꺼내서 확인해보자! -> sysout로 확인해보자! username만 꺼내서 확인해보자!
            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
            System.out.println("로그인 완료됨" + principalDetails.getUsername()); // 값이 있다면 로그인 정상적으로 되었다는 뜻.
            // authentication 객체가 session 영역에 정장을 해야하고 그 방법이 return 해주면 됨.
            // 리턴의 이유는 권한 관리를 security 가 대신 해주기 때문에 편하려고 하는 거임.
            // 굳이 JWT 토큰을 사용하면서 세션을 만들 이유가 없음. 근데 단지 권한 처리 때문에 session에 넣어줍니다.

            // 넣기 직전에 해야 할 것이 있다 바로
            // JWT 토큰 만들기 (이 과정을 여기 정의 말고 아래 정의 successfulAuthentication 메소드에서 수행됨.)

            // 제대로 로그인이 되었다면 아래 값이 반환될 것이다.
            // 이렇게 return 될 때 객체가 session에 저장된다.
            return authenticationToken;

        }catch (Exception e){
            e.printStackTrace();
        }
        // (-------------------------------------------------------------------------------------------------------
        // 2. 정상인지 로그인 시도를 해본다. -> authenticationManager로 로그인 시도를 하면 PrincipalDetailsService가 호출된다.
        // 그러면 PrincipalDetailseService에 있는 loadUserByUsername 메소드가 자동으로 실행된다.
        // 여기서 정상적으로 PrincipalDetails가 return이 되면S
        // 3. 그 principalDetails를 세션에 담고 (oauth2 방식과 다르게 Authentication 에 담지 않고 바로 세션에 담는다 ,,,,)
        // -> 굳이 세션에 담는 이유: 세션에 담지 않으면 권한 관리가 되지 않기 떄문
        //      하지만 권한관리가 필요없는 경우라면 (물론 그런 경우는 거의 없겠지만) principalDetails를 세션에 담지 않아도 된다.
        // 4. jwt 토큰을 만들어서 응답해주면 됨.
        // ------------------------------------------------------------) ━▶ try {} 영역안 주석인데 길어서 아래로 뺀 거임

        // 제대로 로그인이 되지 않았다면 null을 반환한다.
        return null;
    }

    // 실행 순서
    // attemptAuthentication 실행 후 인증이 정상적으로 되었으면 successfulAuthentication 함수가 실행됨
    // jwt 토큰을 만들어서 request 요청한 사용자에게 jwt 토큰을 response해주면 됨.
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        System.out.println("successfulAuthentication 실행된 : 인증이 완료되었다는 뜻임");
        super.successfulAuthentication(request, response, chain, authResult);
    }
}

