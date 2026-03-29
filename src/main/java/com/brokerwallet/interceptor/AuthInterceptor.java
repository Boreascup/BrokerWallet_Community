package com.brokerwallet.interceptor;

import com.brokerwallet.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.crypto.SecretKey;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtProperties jwtProperties;

    public AuthInterceptor(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        String path = request.getRequestURI();

        if (isWhiteList(path)) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(401);
            response.getWriter().write("No token");
            return false;
        }

        String token = authHeader.replace("Bearer ", "");

        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            //存入 request（后续用）
            request.setAttribute("userId", claims.getSubject());
            request.setAttribute("walletAddress", claims.get("walletAddress"));

            return true;

        } catch (Exception e) {
            response.setStatus(401);
            response.getWriter().write("Invalid token");
            return false;
        }
    }

    private boolean isWhiteList(String path) {
        return path.startsWith("/login");
    }
}