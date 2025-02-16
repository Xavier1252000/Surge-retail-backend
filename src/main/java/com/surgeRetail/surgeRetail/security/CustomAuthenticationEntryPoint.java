package com.surgeRetail.surgeRetail.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ApiResponseHandler;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatus;
import com.surgeRetail.surgeRetail.utils.responseHandlers.ResponseStatusCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {


        String expired = (String) request.getAttribute("expired");
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Creating JSON response
        ApiResponseHandler errorResponse;
        if (expired==null){
            errorResponse = new ApiResponseHandler("token expired", null, ResponseStatus.UNAUTHORIZED, HttpServletResponse.SC_UNAUTHORIZED, true);
        }else {
            errorResponse = new ApiResponseHandler("incorrect password", null, ResponseStatus.UNAUTHORIZED, HttpServletResponse.SC_UNAUTHORIZED, true);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}

