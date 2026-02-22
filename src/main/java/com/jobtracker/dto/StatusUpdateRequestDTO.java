package com.jobtracker.dto;

import com.jobtracker.entity.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StatusUpdateRequestDTO {

    @NotNull(message = "New status is required")
    private ApplicationStatus status;

    private String notes;
}
