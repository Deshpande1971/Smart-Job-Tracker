package com.jobtracker.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a Company/Organization profile in the system.
 */
@Entity
@Table(name = "companies")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug; // URL-friendly version of the name

    @Column(nullable = false, length = 2000)
    private String description;

    @Column(nullable = false)
    private String industry;

    private String companySize;
    private String headquarters;

    @Column(nullable = false, unique = true)
    private String contactEmail;

    private String website;
    private String logoUrl;

    @Builder.Default
    private boolean verified = false; // Requires Admin approval

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CompanyStatus status = CompanyStatus.ACTIVE;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
