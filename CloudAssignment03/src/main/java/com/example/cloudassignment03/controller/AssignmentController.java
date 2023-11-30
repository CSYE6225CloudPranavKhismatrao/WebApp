package com.example.cloudassignment03.controller;

import com.example.cloudassignment03.config.CloudWatchMetricsPublisher;
import com.example.cloudassignment03.entity.Account;
import com.example.cloudassignment03.entity.Assignment;
import com.example.cloudassignment03.entity.Submission;
import com.example.cloudassignment03.repository.SubmissionRepository;
import com.example.cloudassignment03.response.AssignmentResponse;
import com.example.cloudassignment03.response.SubmissionResponse;
import com.example.cloudassignment03.services.AssignmentService;
import com.example.cloudassignment03.services.HealthService;
import com.example.cloudassignment03.services.SubmissionService;
import com.example.cloudassignment03.services.ValidationService;
import com.fasterxml.jackson.databind.JsonNode;
import com.timgroup.statsd.StatsDClient;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.boot.actuate.health.Status.UP;

@RestController
@Slf4j
public class AssignmentController {

    private static final String SCHEMA_PATH = "static/schema.json";
    private static final String SUBMISSION_SCHEMA_PATH = "static/submission-schema.json";
    private final AssignmentService assignmentService;
    private final ValidationService validationService;
    private final HealthService healthService;
    private final StatsDClient client;

    @Autowired
    private HikariDataSource hikariDataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SubmissionService submissionService;



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
        List<AssignmentResponse> list = assignmentService.getAll();
        return ResponseEntity.ok(list);
    }

    @PostMapping("/v1/assignments")
    public ResponseEntity<AssignmentResponse> createAssignment(@RequestBody String requestStr){
        String path = "/v1/assignments";
        String method = HttpMethod.POST.toString();
        client.increment("api.calls." + method + path);
        try {
            JsonNode requestJson = validationService.validateJSON(requestStr, SCHEMA_PATH);
            logger.atInfo().log("Validated JSON String");
            Optional<AssignmentResponse> assignment = Optional.of(assignmentService.createAssignment(requestJson));
            logger.atInfo().log("Created Assignment in Database");
            return ResponseEntity.status(201).body(assignment.get());
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
        AssignmentResponse assignment = assignmentService.getOneAssignment(uuid);
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
            if (!assignmentService.updateAssignment(id, requestJson)){
                logger.atError().log("Assignment not found");
                return ResponseEntity.status(404).build();
            }
            logger.atInfo().log("Updated Assignment in Database");
            return ResponseEntity.status(204).build();

    }
    @PostMapping("/v1/assignments/{id}/submission")
    public ResponseEntity<SubmissionResponse> submitAssignment(@RequestBody String requestBody,

//        String path = "/v1/assignments";
//        String method = HttpMethod.PUT.toString();
//        client.increment("api.calls." + method + path);
        JsonNode requestJson = validationService.validateJSON(requestBody, SUBMISSION_SCHEMA_PATH);
        log.atDebug().log("Validated JSON String" + requestJson);
        String header = request.getHeader("Content-Length");
        log.atInfo().log("Content Length: " + header);
        int contentLength = Integer.parseInt(header);
        SubmissionResponse submission = submissionService.submitAssignment(UUID.fromString(id), requestJson, contentLength);

        log.atDebug().log("Submitted Assignment");
        if (submission == null){
            logger.atError().log("Could Not Submit");
            return ResponseEntity.status(404).build();
        }
        log.info("Validated JSON String");
        return ResponseEntity.status(201).body(submission);

    }

//    @GetMapping("healthz")
//    public ResponseEntity<String> getHealthCheck() {
//        try {
//            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
//            client.increment("api.healthCheck.ok");
//            return ResponseEntity.ok().build();
//        } catch (Exception exception) {
//            client.increment("api.healthCheck.failed");
//            return ResponseEntity.status(503).build();
//        }
//    }


}
