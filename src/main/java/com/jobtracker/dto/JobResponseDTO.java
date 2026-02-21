package com.jobtracker.dto;

import com.jobtracker.entity.EmploymentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobResponseDTO {
    private UUID id;
    private UUID companyId;
    private String companyName;
    private String title;
    private String description;
    private String location;
    private EmploymentType employmentType;
    private String experienceLevel;
    private List<String> skills;
    private Double salaryMin;
    private Double salaryMax;
    private LocalDateTime applicationDeadline;
    private LocalDateTime createdAt;
}
