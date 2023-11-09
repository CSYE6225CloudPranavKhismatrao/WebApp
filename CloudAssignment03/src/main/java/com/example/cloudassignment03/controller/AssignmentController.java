package com.example.cloudassignment03.controller;

import com.example.cloudassignment03.config.CloudWatchMetricsPublisher;
import com.example.cloudassignment03.entity.Assignment;
import com.example.cloudassignment03.services.AssignmentService;
import com.example.cloudassignment03.services.HealthService;
import com.example.cloudassignment03.services.ValidationService;
import com.fasterxml.jackson.databind.JsonNode;
import com.timgroup.statsd.StatsDClient;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
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
    private final AssignmentService assignmentService;
    private final ValidationService validationService;
    private final HealthService healthService;
    private final StatsDClient client;

    Logger logger = LoggerFactory.getLogger("jsonLogger");

    public AssignmentController(HealthService healthService, AssignmentService assignmentService, ValidationService validationService, CloudWatchMetricsPublisher cloudWatchMetricsPublisher, StatsDClient client) {
        this.healthService = healthService;
        this.assignmentService = assignmentService;
        this.validationService = validationService;
        this.client = client;
    }


    @GetMapping("/v1/assignments")
    public ResponseEntity<Object> getAll(@RequestBody(required = false) String reqStr, @RequestParam(required = false) String reqPara){
        String path = "/v1/assignments";
        String method = HttpMethod.GET.toString();
        client.increment("api.calls." + method + path);
        if (reqStr != null || reqPara !=null){
            logger.atError().log("Request Body or Request Parameter is not null");

            return ResponseEntity.status(400).build();
        }
        List<Assignment> list = assignmentService.getAll();
        return ResponseEntity.ok(list);
    }

    @PostMapping("/v1/assignments")
    public ResponseEntity<String> createAssignment(@RequestBody String requestStr){
        String path = "/v1/assignments";
        String method = HttpMethod.POST.toString();
        client.increment("api.calls." + method + path);
        try {
            JsonNode requestJson = validationService.validateJSON(requestStr, SCHEMA_PATH);
            logger.atInfo().log("Validated JSON String");
            assignmentService.createAssignment(requestJson);
            logger.atInfo().log("Created Assignment in Database");
            return ResponseEntity.status(201).build();
        }
        catch (Exception e){
            return ResponseEntity.status(400).build();
        }

    }
    @PatchMapping("v1/assignments")
    public ResponseEntity<String> patchAssignment(){
        String path = "/v1/assignments";
        String method = HttpMethod.PATCH.toString();
        client.increment("api.calls." + method + path);
        logger.atError().log("PATCH Method not allowed");

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }

    @GetMapping("/v1/assignments/{id}")
    public ResponseEntity<Object> getOne(@PathVariable String id){
        String path = "/v1/assignments/{id}";
        String method = HttpMethod.GET.toString();
        client.increment("api.calls." + method + "ONE" + path);
        UUID uuid = UUID.fromString(id);
        Assignment assignment = assignmentService.getOneAssignment(uuid);
        if (assignment == null){
            logger.atError().log("Assignment not found");
            return ResponseEntity.status(404).build();
        }
        return ResponseEntity.status(200).body(assignment);
    }

    @DeleteMapping("/v1/assignments/{id}")
    public ResponseEntity<Object> deleteAssignment(@PathVariable String id){
        String path = "/v1/assignments";
        String method = HttpMethod.DELETE.toString();
        client.increment("api.calls." + method + path);
        UUID uuid = UUID.fromString(id);
        logger.atInfo().log("Deleted Assignment in Database");
        return assignmentService.deleteAssignment(uuid) ?
                ResponseEntity.status(204).build() : ResponseEntity.status(404).build();
    }


    @PutMapping("/v1/assignments/{id}")
    public ResponseEntity<Object> updateAssignments(@RequestBody String requestBody,
                                                    @PathVariable String id){
        String path = "/v1/assignments";
        String method = HttpMethod.PUT.toString();
        client.increment("api.calls." + method + path);
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
                logger.atError().log("Assignment not found");
                return ResponseEntity.status(404).build();
            }
            logger.atInfo().log("Updated Assignment in Database");
            return ResponseEntity.status(204).build();

    }

    @GetMapping("/healthz")
    public ResponseEntity<String> getHealthCheck() {
        String path = "/healthz";
        String method = HttpMethod.GET.toString();
        client.increment("api.calls." + method + path);
        try {
            if (healthService.checkDatabaseConnection()){
                logger.atInfo().log("Database is up and running");
                return ResponseEntity.ok()
                        .header("Cache-Control","no-cache, no-store, must-revalidate")
                        .header("Pragma", "no-cache")
                        .header("X-Content-Type-Options", "nosniff")
                        .build();
            }
            logger.atError().log("Database is down");
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
