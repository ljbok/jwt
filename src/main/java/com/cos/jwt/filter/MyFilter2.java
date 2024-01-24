package com.cos.jwt.filter;

import javax.servlet.*;
import java.io.IOException;

public class MyFilter2 implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("필터2");
        /*
        // 이렇게만 작성하면 프로젝트가 이 필터 거치고 안녕만 출력되고 끝나버림
        // 그냥 예시 보여주려고 작성한 부분임 신경 크게 쓰지 말 것
        // 끝나지 않고 계속 진행해주려면 filterChain에 넘겨주어야 한다!
        PrintWriter out = servletResponse.getWriter();
        out.println("안녕");
        */
        filterChain.doFilter(servletRequest,servletResponse); // 이렇게 하면 필터 거친 후에도 종료되지 않고 유지된다!

    }
}
