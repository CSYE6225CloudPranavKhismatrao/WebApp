package com.example.cloudassignment03.response;

import com.example.cloudassignment03.entity.Submission;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class AssignmentResponse {
    @JsonProperty("name")
    private String name;
    @JsonProperty("points")
    private int points;
    @JsonProperty("num_of_attempts")
    private int num_of_attempts;
    @JsonProperty("id")
    private UUID id;
    @JsonProperty("deadline")
    private LocalDateTime deadline;
    @JsonProperty("assignment_created")
    private LocalDateTime assignmentCreated;
    @JsonProperty("assignment_updated")
    private LocalDateTime assignmentUpdated;
}
