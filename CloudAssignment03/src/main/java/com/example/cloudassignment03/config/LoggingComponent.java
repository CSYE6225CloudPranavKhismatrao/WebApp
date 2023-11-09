package com.example.cloudassignment03.config;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.io.IOException;


@Component
@Slf4j
public class LoggingComponent extends HttpFilter {
    Logger logger = LoggerFactory.getLogger("jsonLogger");

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        logger.atInfo().log("Incoming Request " + request.getMethod() + " " + request.getRequestURI() ); // info("Incoming Request: " + request.getMethod() + " " + request.getRequestURI());

        chain.doFilter(request, response);
        logger.atInfo().log("Outgoing Response: " + response.getStatus());
    }

//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
//        HttpServletRequest httpRequest = (HttpServletRequest) request;
//        System.out.println("Incoming request: " + httpRequest.getMethod() + " " + httpRequest.getRequestURI());
//        chain.doFilter(request, response);
//    }
}
