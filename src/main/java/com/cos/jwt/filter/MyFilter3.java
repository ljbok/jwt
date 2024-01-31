package com.cos.jwt.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class MyFilter3 implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        // 필터 테스트를 요청 타입이
        // POST 일때만 콘솔창에 실행시킬 거임 --> PostMan 으로 테스트하기 위함
        // 임의 토큰 : cos(코스)
        // 위 토큰이 있을 때만 필터 계속 수행

        // 필터 테스트를 요청 타입이
        // POST 일때만 콘솔창에 실행시킬 거임 --> PostMan 으로 테스트하기 위함
        if (req.getMethod().equals("POST")){
            String headerAuth = req.getHeader("Authorization");
            System.out.println(headerAuth);
            System.out.println("필터3");

            // header 로 들어온 토큰이 코스일때만 다음 필터로
            // 넘겨 프로젝트 정상 실행

            //우리는 앞으로 뭐를 만들어야 하냐면
            //cos라는 토큰을 만들어주어야 한다
            //언제 만들어줘야 하냐면 아이디와 패스워드가  정상적으로 들어와서
            //로그인이 완료되면 토큰을 만들어주고 그걸 응답을 해준다
            // 요청할 때 마다 header에 Authorization에 value 값으로 토큰을 가지고 오겠죠?
            // 그때 토큰이 넘어오면 이 토큰이 내가 만든 토큰이 맞는지만 검증하면 된다. --> cos인지 볼 필요가 없다 (RSA, HS256)
            if (headerAuth.equals("cos")) {
                filterChain.doFilter(req, resp);
            }else { // 코스가 아니라면 필터 단계에서 프로젝트 중단
                PrintWriter outPrintWriter = resp.getWriter();
                outPrintWriter.println("인증안됨");
            }
        }
    }
}
