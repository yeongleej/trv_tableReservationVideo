package com.ssafy.jariyo.global.jwt.filter;

import com.ssafy.jariyo.global.jwt.util.ErrorMessage;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        try {
            chain.doFilter(request, response);
        } catch (Exception ex) {
            log.info("JwtException Error 발생 : "+ex.getMessage());
            String message = ex.getMessage();
            if(message.equals(ErrorMessage.NO_VALUE_PRESENT.getMessage())){
                // 토큰이 없을 때
                setResponse(response, ErrorMessage.NO_VALUE_PRESENT);
            } else if(message.equals(ErrorMessage.BLACKLISTED_TOKENS.getMessage())) {
                // 블랙리스트 등록된 토큰일 때
                setResponse(response, ErrorMessage.BLACKLISTED_TOKENS);
            } else if(message.equals(ErrorMessage.MISSING_PARTS.getMessage())) {
                // 토큰의 일부가 사라졌을 때
                setResponse(response, ErrorMessage.MISSING_PARTS);
            } else if(message.contains(ErrorMessage.EXPIRED_TOKEN.getMessage())){
                // 만료된 토큰일 때
                setResponse(response, ErrorMessage.EXPIRED_TOKEN);
            } else {
                // 그 외
                setResponse(response, ErrorMessage.UNKNOWN_ERROR);
            }
        }
    }

    private void setResponse(HttpServletResponse response, ErrorMessage errorMessage) throws RuntimeException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(errorMessage.getCode());
        response.getWriter().print(errorMessage.getMessage());
    }
}
