package com.example.cloudassignment03.entity;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SNSMessage {
    @JsonProperty("submissionUrl")
    private String submissionUrl;
    @JsonProperty("status")
    private String status;
    @JsonProperty("userEmail")
    private String userEmail;
    @JsonProperty("assignmentId")
    private String assignmentId;
}
