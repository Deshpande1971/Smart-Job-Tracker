package com.jobtracker.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationRequestDTO {

    @NotBlank(message = "Resume URL is required")
    private String resumeUrl;

    private String coverLetter;
}
