package com.jobtracker.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Represents a Job Vacancy posted by a Company.
 */
@Entity
@Table(name = "job_postings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPosting {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 5000)
    private String description;

    private String location;

    @Enumerated(EnumType.STRING)
    private EmploymentType employmentType;

    private String experienceLevel;

    @ElementCollection
    @CollectionTable(name = "job_skills", joinColumns = @JoinColumn(name = "job_id"))
    @Column(name = "skill")
    private List<String> skills;

    private Double salaryMin;
    private Double salaryMax;

    private LocalDateTime applicationDeadline;

    @Builder.Default
    private boolean isPublished = true;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
