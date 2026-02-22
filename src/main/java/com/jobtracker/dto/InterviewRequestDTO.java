package com.jobtracker.dto;

import com.jobtracker.entity.InterviewMode;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewRequestDTO {

    @NotNull(message = "Start time is required")
    @Future(message = "Start time must be in the future")
    private LocalDateTime slotStart;

    @NotNull(message = "End time is required")
    @Future(message = "End time must be in the future")
    private LocalDateTime slotEnd;

    @NotNull(message = "Interview mode is required")
    private InterviewMode mode;

    @NotBlank(message = "Location or Link is required")
    private String locationOrLink;
}
