package datn.service.parking.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import datn.service.parking.dto.ApiResponse;
import datn.service.parking.exception.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {



    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,  AuthenticationException authException) throws IOException, ServletException {
       try {
           ErrorCode errorCode = ErrorCode.UNAUTHENTICATED;

           response.setStatus(errorCode.getStatusCode().value());
           response.setContentType(MediaType.APPLICATION_JSON_VALUE);

           ApiResponse<?> apiResponse = ApiResponse.builder()
                   .code(errorCode.getCode())
                   .message(errorCode.getMessage())
                   .build();
           ObjectMapper objectMapper = new ObjectMapper();
           response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
           response.flushBuffer();
       } catch (Exception e) {
           throw new RuntimeException(e);
       }
    }
}
