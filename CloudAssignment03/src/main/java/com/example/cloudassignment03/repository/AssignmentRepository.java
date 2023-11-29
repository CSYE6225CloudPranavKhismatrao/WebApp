package com.example.cloudassignment03.repository;

import com.example.cloudassignment03.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment,Long> {

    Assignment findById(UUID id);

    void deleteById(UUID id);

//    Assignment findByIdAndOwnerEmail(UUID id, String ownerEmail);
//
//    boolean existsById(UUID id);
//
//    @Transactional
//    Assignment deleteById(UUID uuid);
}
