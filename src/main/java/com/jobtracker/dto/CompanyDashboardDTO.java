package com.jobtracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyDashboardDTO {
    private UUID companyId;
    private long totalJobsPosted;
    private long totalApplicants;
    private Map<String, Long> applicantsByStatus;
}
