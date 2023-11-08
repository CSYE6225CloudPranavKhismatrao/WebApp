package com.example.cloudassignment03.services;

import com.example.cloudassignment03.auth.BasicAuthenticationManager;
import com.example.cloudassignment03.entity.Assignment;
import com.example.cloudassignment03.exceptions.AssignmentNotFoundException;
import com.example.cloudassignment03.exceptions.CannotAccessException;

import com.example.cloudassignment03.repository.AssignmentRepository;
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
    public void createAssignment(JsonNode reqNode){
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
        assignmentRepository.save(assignment);
        log.info("SAVED ASSIGNMENT TO DATABASE");
    }


    @Override
    public Assignment getOneAssignment(UUID id){
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
        return assignment;
    }

    @Override
    public List<Assignment> getAll() {
        return assignmentRepository.findAll();
    }

    @Override
    @Transactional
    public boolean deleteAssignment(UUID uuid) {
        Assignment assignment = assignmentRepository.findById(uuid);
        log.info(String.valueOf(assignment));
        if (assignment ==  null)
            throw new AssignmentNotFoundException("Assignment not found");
        if (assignment.getOwnerEmail().equals(SecurityContextHolder.getContext().getAuthentication().getName())){
            Query query = entityManager.createQuery("delete from Assignment a WHERE a.id=:id");
            query.setParameter("id", uuid);
            query.executeUpdate();
            log.info("SUCCESSFULLY DELETED");
            return true;
        }
        log.warn("CANNOT ACCESS DATA");
        throw new CannotAccessException("Cannot access the requested Data");
    }

    @Override
    public boolean updateAssignment(UUID id, Assignment requestBody) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Assignment assignment1 = assignmentRepository
                .findById(id);
        if (assignment1 == null){
            throw new AssignmentNotFoundException("Not Found");
        }
        if (authentication.getPrincipal().equals(assignment1.getOwnerEmail())){
            assignment1.setName(requestBody.getName());
            assignment1.setPoints(requestBody.getPoints());
            assignment1.setNum_of_attempts(requestBody.getNum_of_attempts());
            assignmentRepository.save(assignment1);
            log.info("UPDATED SUCCESSFULLY");
            return true;
        }
        else
            throw new CannotAccessException("Cannot access the requested Data");


    }

}




