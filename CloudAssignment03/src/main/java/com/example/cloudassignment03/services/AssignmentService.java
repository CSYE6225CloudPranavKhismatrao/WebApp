package com.example.cloudassignment03.services;

import com.example.cloudassignment03.auth.BasicAuthenticationManager;
import com.example.cloudassignment03.entity.Assignment;
import com.example.cloudassignment03.exceptions.AssignmentNotFoundException;
import com.example.cloudassignment03.exceptions.CannotAccessException;

import com.example.cloudassignment03.repository.AssignmentRepository;
import com.example.cloudassignment03.response.AssignmentResponse;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class AssignmentService implements IAssignmentService{
    private final AssignmentRepository assignmentRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private final BasicAuthenticationManager authenticationManager;

    public AssignmentService(AssignmentRepository assignmentRepository, BasicAuthenticationManager authenticationManager) {
        this.assignmentRepository = assignmentRepository;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public AssignmentResponse createAssignment(JsonNode reqNode){
        Assignment assignment = new Assignment();
        assignment.setName(reqNode.get("name").textValue());
        assignment.setPoints(reqNode.get("points").intValue());
        assignment.setNum_of_attempts(reqNode.get("num_of_attempts").intValue());
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
//        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyy", Locale.ENGLISH);
        LocalDateTime date = LocalDateTime.parse(reqNode.get("deadline").textValue(), inputFormatter);
//        String formattedDate = outputFormatter.format(date);
        assignment.setDeadline(date);
        assignment.setAssignmentCreated(LocalDateTime.now());
        assignment.setAssignmentUpdated(LocalDateTime.now());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication(); //to get Authentication
        assignment.setOwnerEmail(authentication.getName());
        assignment = assignmentRepository.save(assignment);
        log.info("SAVED ASSIGNMENT TO DATABASE");
        return mapAssignmentToResponse(assignment);
    }


    @Override
    public AssignmentResponse getOneAssignment(UUID id){
        if (id == null) {
            log.warn("INVALID USER");
            throw new IllegalArgumentException("Invalid User");
        }

        Optional<Assignment> optionalUser = Optional.ofNullable(assignmentRepository.findById(id));

        Assignment assignment = optionalUser.orElseThrow(() -> new AssignmentNotFoundException("Assignment not found"));
        if (!assignment.getOwnerEmail().equals(SecurityContextHolder.getContext().getAuthentication().getName())){
            log.warn("CANNOT ACCESS RESOURCE");
            throw new CannotAccessException("Cannot access requested resource");
        }
        log.info("RETRIEVED REQUESTED ASSIGNMENT");
        return mapAssignmentToResponse(assignment);
    }

    @Override
    public List<AssignmentResponse> getAll() {
        List<Assignment> assignments = assignmentRepository.findAll();
        return assignments.stream().map(this::mapAssignmentToResponse).toList();
    }

    @Override
    @Transactional
    public boolean deleteAssignment(UUID uuid) {
        Assignment assignment = assignmentRepository.findById(uuid);
        log.info(String.valueOf(assignment));
        if (assignment ==  null)
            throw new AssignmentNotFoundException("Assignment not found");
        if (assignment.getOwnerEmail().equals(SecurityContextHolder.getContext().getAuthentication().getName())){
//            Query query = entityManager.createQuery("delete from Assignment a WHERE a.id=:id");
//            query.setParameter("id", uuid);
//            query.executeUpdate();
            assignmentRepository.deleteById(uuid);
            log.info("SUCCESSFULLY DELETED");
            return true;
        }
        log.warn("CANNOT ACCESS DATA");
        throw new CannotAccessException("Cannot access the requested Data");
    }

    @Override
    public boolean updateAssignment(String id, JsonNode requestJson) {
        UUID uuid = UUID.fromString(id);
        Assignment new_assignment = new Assignment();
        new_assignment.setName(requestJson.get("name").textValue());
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
//        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyy", Locale.ENGLISH);
        LocalDateTime date = LocalDateTime.parse(requestJson.get("deadline").textValue(), inputFormatter);
//        String formattedDate = outputFormatter.format(date);
        new_assignment.setDeadline(date);
        new_assignment.setPoints(requestJson.get("points").intValue());
        new_assignment.setNum_of_attempts(requestJson.get("num_of_attempts").intValue());
        new_assignment.setAssignmentUpdated(LocalDateTime.now());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Assignment assignment1 = assignmentRepository
                .findById(uuid);
        if (assignment1 == null){
            throw new AssignmentNotFoundException("Not Found");
        }
        if (authentication.getPrincipal().equals(assignment1.getOwnerEmail())){
            assignment1.setName(new_assignment.getName());
            assignment1.setPoints(new_assignment.getPoints());
            assignment1.setNum_of_attempts(new_assignment.getNum_of_attempts());
            assignment1.setDeadline(new_assignment.getDeadline());
            assignment1.setAssignmentUpdated(new_assignment.getAssignmentUpdated());
            assignmentRepository.save(assignment1);
            log.info("UPDATED SUCCESSFULLY");
            return true;
        }
        else
            throw new CannotAccessException("Cannot access the requested Data");


    }

    private AssignmentResponse mapAssignmentToResponse(Assignment assignment){
        AssignmentResponse assignmentResponse = new AssignmentResponse();
        assignmentResponse.setId(assignment.getId());
        assignmentResponse.setName(assignment.getName());
        assignmentResponse.setPoints(assignment.getPoints());
        assignmentResponse.setNum_of_attempts(assignment.getNum_of_attempts());
        assignmentResponse.setDeadline(assignment.getDeadline());
        assignmentResponse.setAssignmentCreated(assignment.getAssignmentCreated());
        assignmentResponse.setAssignmentUpdated(assignment.getAssignmentUpdated());
        return assignmentResponse;
    }

}




