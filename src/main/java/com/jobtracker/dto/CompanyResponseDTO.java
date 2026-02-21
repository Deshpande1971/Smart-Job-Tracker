package com.jobtracker.dto;

import com.jobtracker.entity.CompanyStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyResponseDTO {
    private UUID id;
    private String name;
    private String slug;
    private String description;
    private String industry;
    private String contactEmail;
    private boolean verified;
    private CompanyStatus status;
}
