package com.example.cloudassignment03.services;

import com.example.cloudassignment03.entity.Submission;
import com.example.cloudassignment03.response.SubmissionResponse;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletRequest;

import java.util.UUID;

public interface ISubmisionService {


    SubmissionResponse submitAssignment(UUID id, JsonNode requestBody, int contentLength);
}
