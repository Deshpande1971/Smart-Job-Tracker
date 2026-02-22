package com.jobtracker.dto;

import com.jobtracker.entity.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationResponseDTO {
    private UUID id;
    private UUID jobId;
    private String jobTitle;
    private String companyName;
    private UUID applicantId;
    private String applicantName;
    private String resumeUrl;
    private String coverLetter;
    private ApplicationStatus status;
    private LocalDateTime appliedAt;
    private String recruiterNotes;
}
