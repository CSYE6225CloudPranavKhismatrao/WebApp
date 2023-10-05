package com.example.cloudassignment03.controller;

import com.example.cloudassignment03.entity.Assignment;
import com.example.cloudassignment03.services.AssignmentService;
import com.example.cloudassignment03.services.HealthService;
import com.example.cloudassignment03.services.ValidationService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@RestController
@Slf4j
public class AssignmentController {

    private static final String SCHEMA_PATH = "static/schema.json";
    @Autowired
    private AssignmentService assignmentService;
    @Autowired
    private ValidationService validationService;
    @Autowired
    private final HealthService healthService;

    public AssignmentController(HealthService healthService) {
        this.healthService = healthService;
    }


    @GetMapping("/v1/assignments")
    public ResponseEntity<Object> getAll(@RequestBody(required = false) String reqStr, @RequestParam(required = false) String reqPara){
        if (reqStr != null || reqPara !=null){

            return ResponseEntity.status(400).build();
        }
        List<Assignment> list = assignmentService.getAll();
        return ResponseEntity.ok(list);
    }

    @PostMapping("/v1/assignments")
    public ResponseEntity<String> createAssignment(@RequestBody(required = true) String requestStr){
        try {
            JsonNode requestJson = validationService.validateJSON(requestStr, SCHEMA_PATH);
            log.info("Validated JSON String");
            assignmentService.createAssignment(requestJson);
            log.info("Created Assignment in Database");
            return ResponseEntity.status(201).build();
        }
        catch (Exception e){
            log.error(e.getMessage());
            return ResponseEntity.status(400).build();
        }

    }

    @GetMapping("/v1/assignments/{id}")
    public ResponseEntity<Object> getOne(@PathVariable String id){
        UUID uuid = UUID.fromString(id);
        Assignment assignment = assignmentService.getOneAssignment(uuid);

        return ResponseEntity.status(200).body(assignment);
    }

    @DeleteMapping("/v1/assignments/{id}")
    public ResponseEntity<Object> deleteAssignment(@PathVariable String id){
        UUID uuid = UUID.fromString(id);
        return assignmentService.deleteAssignment(uuid) ?
                ResponseEntity.status(204).build() : ResponseEntity.status(404).build();
    }


    @PutMapping("/v1/assignments/{id}")
    public ResponseEntity<Object> updateAssignments(@RequestBody String requestBody,
                                                    @PathVariable String id){
        try {
            JsonNode requestJson = validationService.validateJSON(requestBody, SCHEMA_PATH);
            log.info("Validated JSON String");
            UUID uuid = UUID.fromString(id);
            Assignment assignment = new Assignment();
            assignment.setName(requestJson.get("name").textValue());
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
//        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyy", Locale.ENGLISH);
            LocalDateTime date = LocalDateTime.parse(requestJson.get("deadline").textValue(), inputFormatter);
//        String formattedDate = outputFormatter.format(date);
          assignment.setDeadline(date);
           assignment.setPoints(requestJson.get("points").intValue());
            assignment.setAssignmentUpdated(LocalDateTime.now());
            if (!assignmentService.updateAssignment(uuid, assignment)){
                return ResponseEntity.status(404).build();
            }
            log.info("Updated Assignment in Database");
            return ResponseEntity.status(204).build();
        }
        catch (Exception e){
            log.error(e.getMessage());
            return ResponseEntity.status(400).build();
        }
    }

    @GetMapping("/healthz")
    public ResponseEntity<String> getHealthCheck() {
        try {
//           dataSource.getConnection().prepareStatement("SELECT 1").execute();
            if (healthService.checkDatabaseConnection()){
                return ResponseEntity.ok()
                        .header("Cache-Control","no-cache, no-store, must-revalidate")
                        .header("Pragma", "no-cache")
                        .header("X-Content-Type-Options", "nosniff")
                        .build();
            }
            return ResponseEntity.status(503)
                    .header("Cache-Control","no-cache, no-store, must-revalidate")
                    .header("Pragma", "no-cache")
                    .header("X-Content-Type-Options", "nosniff")
                    .build();

        } catch (Exception e) {
            return ResponseEntity.status(503)
                    .build();
        }

    }



}
