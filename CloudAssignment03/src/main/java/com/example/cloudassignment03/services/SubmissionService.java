package com.example.cloudassignment03.services;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.PublishRequest;
import com.example.cloudassignment03.entity.Assignment;
import com.example.cloudassignment03.entity.SNSMessage;
import com.example.cloudassignment03.entity.Submission;
import com.example.cloudassignment03.exceptions.AssignmentNotFoundException;
import com.example.cloudassignment03.exceptions.CannotAccessException;
import com.example.cloudassignment03.exceptions.CannotSubmitException;
import com.example.cloudassignment03.repository.AssignmentRepository;
import com.example.cloudassignment03.repository.SubmissionRepository;
import com.example.cloudassignment03.response.SubmissionResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
@Service
@Slf4j
public class SubmissionService implements ISubmisionService{

    @Value("${TOPIC_ARN}")
    private String TOPIC;
    private final AssignmentRepository assignmentRepository;

    private final SubmissionRepository submissionRepository;

    @Value("${AWS_SECRET_ACCESS_KEY}")
    private String AWS_SECRET_ACCESS_KEY;


    @Value("${AWS_ACCESS_KEY_ID}")
    private String AWS_ACCESS_KEY_ID;



    public SubmissionService(AssignmentRepository assignmentRepository, SubmissionRepository submissionRepository) {
        this.assignmentRepository = assignmentRepository;
        this.submissionRepository = submissionRepository;

    }

    @Override
    public SubmissionResponse submitAssignment(UUID id, JsonNode requestBody) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Assignment assignment1 = assignmentRepository
                .findById(id);
        log.atDebug().log("Assignment: {}", assignment1);
        if (assignment1 == null) {
            throw new AssignmentNotFoundException("Not Found");
        }
//        if (!authentication.getPrincipal().equals(assignment1.getOwnerEmail())) {
//            throw new CannotAccessException("Cannot access the requested Data");
//        }
        if (assignment1.getNum_of_attempts() < assignment1.getSubmissions().stream().filter(submission -> submission.getAccountEmail().equals(authentication.getName())).count()){
            throw new CannotSubmitException("No more attempts left");
        }
        if (assignment1.getDeadline().isBefore(LocalDateTime.now())){
            throw new CannotSubmitException("Deadline has passed");
        }
        assignment1.setAssignmentUpdated(LocalDateTime.now());
        assignmentRepository.save(assignment1);
        Submission submission = new Submission();
        submission.setAssignment(assignment1);
        submission.setSubmissionLink(requestBody.get("submission_url").textValue());
        submission.setSubmissionDate(LocalDateTime.now());
        submission.setSubmissionUpdated(LocalDateTime.now());
        submission.setAccountEmail(authentication.getName());
        submission = submissionRepository.save(submission);
        log.atDebug().log("Submission: {}", submission);

//        BasicAWSCredentials credentials = new BasicAWSCredentials(
//                "AKIA55GFDRQSLWJDJK7T",
//                "URWJFg9+BXqJIgS+O4Qe/x91njRlrbXLfMb8WBFz"
//        );
//============================================ SNS CODE ==============================================================
        Regions regions = Regions.US_EAST_1;

        val snsClient = AmazonSNSClient.builder()
                .withRegion(regions).withCredentials(new AWSCredentialsProvider() {
                    @Override
                    public void refresh() {

                    }
                    @Override
                    public com.amazonaws.auth.AWSCredentials getCredentials() {
                        return new BasicAWSCredentials(
                                AWS_ACCESS_KEY_ID,
                                AWS_SECRET_ACCESS_KEY
                        );
                    }
                }).build();
        log.atDebug().log("SNS Client: {}", snsClient);
        SNSMessage snsMessage = new SNSMessage();
        snsMessage.setSubmissionUrl(submission.getSubmissionLink());
        snsMessage.setStatus("SUCCESS");
        snsMessage.setUserEmail(authentication.getName());
        snsMessage.setAssignmentId(String.valueOf(assignment1.getId()));
        ObjectMapper objectMapper = new ObjectMapper();
        String json = null;
        try {
            json = objectMapper.writeValueAsString(snsMessage);
            log.atInfo().log("JSON: {}", json);
        } catch (JsonProcessingException e) {
            log.atError().log("Error: {}", e);
            log.atDebug().log("Error: {}", e);
            throw new RuntimeException(e);
        }
        PublishRequest publishRequest = new PublishRequest(TOPIC, json);

        snsClient.publish(publishRequest);

//        ==================================================================================================================

        log.atInfo().log("SUBMITTED ASSIGNMENT", json);
        log.atInfo().log("SENT NOTIFICATION", publishRequest);

        log.info("UPDATED SUCCESSFULLY");
        return mapSubmissionToResponse(submission);
    }

    SubmissionResponse mapSubmissionToResponse(Submission submission){
        SubmissionResponse submissionResponse = new SubmissionResponse();
        submissionResponse.setId(submission.getId());
        submissionResponse.setSubmissionLink(submission.getSubmissionLink());
        submissionResponse.setSubmissionDate(submission.getSubmissionDate());
        submissionResponse.setSubmissionUpdated(submission.getSubmissionUpdated());
        submissionResponse.setAssignmentId(submission.getAssignment().getId());
        return submissionResponse;
    }
}
