package com.example.cloudassignment03.services;

import com.example.cloudassignment03.controller.AssignmentController;
import com.example.cloudassignment03.exceptions.JsonFormatException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

@Service
@Slf4j
public class ValidationServiceImpl implements ValidationService{
    @Override
    public JsonNode validateJSON(String json, String schemaPath) {
        JsonNode jsonNode;
        try(InputStream schemaStream = AssignmentController.class.getClassLoader().getResourceAsStream(schemaPath)) {
            JsonSchema jsonSchema = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7).getSchema(schemaStream);

            // READING AND PARSING DATA
            ObjectMapper objectMapper =new ObjectMapper();
            jsonNode = objectMapper.readTree(json);

            Set<ValidationMessage> errors = jsonSchema.validate(jsonNode);
            StringBuilder errorsCombined = new StringBuilder();

            for (ValidationMessage error : errors) {
                log.error("Validation Error: {}", error);
                errorsCombined.append(error.toString()).append("\n");
            }
            if (!errors.isEmpty())
                throw new JsonFormatException("Improper json format: \n" + errorsCombined);
        }
        catch (JsonProcessingException e) {
            throw new JsonFormatException("Failed to parse the body into json");
        }
        catch (IOException e) {
            throw new JsonSchemaException("Error occurred while fetching the schema from resource");
        }
        return jsonNode;
    }
}
