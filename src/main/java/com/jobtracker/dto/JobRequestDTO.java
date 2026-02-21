package com.jobtracker.dto;

import com.jobtracker.entity.EmploymentType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobRequestDTO {

    @NotBlank(message = "Job title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    private String location;
    private EmploymentType employmentType;
    private String experienceLevel;
    private List<String> skills;
    private Double salaryMin;
    private Double salaryMax;

    @Future(message = "Deadline must be in the future")
    private LocalDateTime applicationDeadline;

    @Builder.Default
    private boolean isPublished = true;
}
