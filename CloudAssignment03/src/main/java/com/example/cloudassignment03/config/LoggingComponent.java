package com.example.cloudassignment03.config;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.io.IOException;


@Component
@Slf4j
public class LoggingComponent extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        log.info("Incoming Request: " + request.getMethod() + " " + request.getRequestURI());
    // .addKeyValue("Incoming Request", request.getMethod())
                        //.addKeyValue("URI", request.getRequestURI()).log("Message");
        chain.doFilter(request, response);
        log.info("Outgoing Response: " + response.getStatus());
//        log.atInfo().addKeyValue("Outgoing Response", response.getStatus()).log("Message");
    }

//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
//        HttpServletRequest httpRequest = (HttpServletRequest) request;
//        System.out.println("Incoming request: " + httpRequest.getMethod() + " " + httpRequest.getRequestURI());
//        chain.doFilter(request, response);
//    }
}
