package com.example.cloudassignment03.services;

import com.example.cloudassignment03.entity.Assignment;
import com.example.cloudassignment03.response.AssignmentResponse;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.UUID;

public interface IAssignmentService {
    AssignmentResponse createAssignment(JsonNode reqNode);

    AssignmentResponse getOneAssignment(UUID id);

    List<AssignmentResponse> getAll();

    boolean deleteAssignment(UUID uuid);


    boolean updateAssignment(String id, JsonNode requestJson);
}
