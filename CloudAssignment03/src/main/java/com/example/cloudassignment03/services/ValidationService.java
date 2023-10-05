package com.example.cloudassignment03.services;

import com.fasterxml.jackson.databind.JsonNode;

public interface ValidationService {
    JsonNode validateJSON(String json, String schemaPath);

}
