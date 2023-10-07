package com.example.cloudassignment03;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HealthCheckIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testHealthCheckEndpoint() {
        String url = "http://localhost:" + port + "/healt";
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);

        // Assert that the response status code is 200 OK
        assertEquals(200, responseEntity.getStatusCodeValue());
    }
}