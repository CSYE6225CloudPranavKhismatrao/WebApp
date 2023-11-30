package com.example.cloudassignment03.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class SubmissionResponse {
    @JsonProperty("id")
    private UUID id;
    @JsonProperty("submission_url")
    private String submissionLink;
    @JsonProperty("submission_date")
    private LocalDateTime submissionDate;
    @JsonProperty("submission_updated")
    private LocalDateTime submissionUpdated;
    @JsonProperty("assignment_id")
    private UUID assignmentId;
}
