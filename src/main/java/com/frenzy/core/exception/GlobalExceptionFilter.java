package com.frenzy.core.exception;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class GlobalExceptionFilter extends OncePerRequestFilter {

//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//        try {
//            filterChain.doFilter(request, response);
//        } catch (Exception e) {
//            // 捕获全局异常并处理
//            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//            response.setContentType("application/json");
//            response.getWriter().write("{\"code\":\"500\", \"message\":\"" + e.getMessage() + "\"}");
//        }
//    }

    @Override
    protected void doFilterInternal(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, jakarta.servlet.FilterChain filterChain) throws jakarta.servlet.ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            // 捕获全局异常并处理
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"code\":\"500\", \"message\":\"" + e.getMessage() + "\"}");
        }
    }
}

