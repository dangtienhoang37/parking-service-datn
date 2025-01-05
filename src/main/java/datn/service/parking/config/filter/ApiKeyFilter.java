package datn.service.parking.config.filter;



import com.fasterxml.jackson.databind.ObjectMapper;
import datn.service.parking.dto.response.ApiResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ApiKeyFilter extends OncePerRequestFilter {
    private static final String API_KEY_HEADER = "X-api-key";
    private static final String API_KEY = "internalApikey";
    @Autowired
    private ObjectMapper objectMapper;




    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String apiKey = request.getHeader(API_KEY_HEADER);
        if(apiKey == null || !apiKey.equals(API_KEY) ){
            ApiResponse res = new ApiResponse<>().builder()
                    .code(1001)
                    .message("wrong api key")
                    .isSucess(false)
                    .build();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            objectMapper.writeValue(response.getWriter(),res);
            return ;
        }
        filterChain.doFilter(request,response);
    }
}
