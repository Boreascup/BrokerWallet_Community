package com.brokerwallet.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        String token = request.getHeader("token");

        // 简单校验（调试阶段）
        if (token == null || token.isEmpty()) {
            response.setStatus(401);
            response.getWriter().write("No token");
            return false;
        }

        return true;
    }
}