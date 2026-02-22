package com.jobtracker.dto;

import com.jobtracker.entity.InterviewMode;
import com.jobtracker.entity.InterviewStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewResponseDTO {
    private UUID id;
    private UUID applicationId;
    private String jobTitle;
    private String companyName;
    private LocalDateTime slotStart;
    private LocalDateTime slotEnd;
    private InterviewMode mode;
    private String locationOrLink;
    private InterviewStatus status;
}
