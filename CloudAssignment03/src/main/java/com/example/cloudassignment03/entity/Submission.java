package com.example.cloudassignment03.entity;

import com.ethlo.time.DateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "submission_url")
    private String submissionLink;

    @Column(name = "submission_date")
    @JsonFormat(pattern="YYYY-MM-DD'T'HH:MM:SS.SSS'Z'",shape = JsonFormat.Shape.STRING, locale = "en_GB")
    private LocalDateTime submissionDate;

    @Column(name = "submission_updated")
    @JsonFormat(pattern="YYYY-MM-DD'T'HH:MM:SS.SSS'Z'",shape = JsonFormat.Shape.STRING, locale = "en_GB")
    private LocalDateTime submissionUpdated;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    @Column(name = "account_email")
    private String accountEmail;

}
