package com.jobtracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeekerDashboardDTO {
    private long totalApplications;
    private long interviewCount;
    private long offerCount;
    private long rejectedCount;
    private Map<String, Long> statusBreakdown;
}
