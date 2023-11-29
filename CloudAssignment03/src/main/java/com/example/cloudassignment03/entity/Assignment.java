package com.example.cloudassignment03.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "Assignment")
public class Assignment {

    @Column
    private String name;
    @Column
    private int points;
    @Column
    private int num_of_attempts;
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column
    @JsonFormat(pattern="YYYY-MM-DD'T'HH:MM:SS.SSS'Z'",shape = JsonFormat.Shape.STRING, locale = "en_GB")
    private LocalDateTime deadline;
    @Column
    private LocalDateTime assignmentCreated;
    @Column
    private LocalDateTime assignmentUpdated;
    @Column
    private String ownerEmail;
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "assignment")
    private List<Submission> submissions;

}
