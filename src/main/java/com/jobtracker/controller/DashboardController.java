package com.jobtracker.controller;

import com.jobtracker.dto.CompanyDashboardDTO;
import com.jobtracker.dto.SeekerDashboardDTO;
import com.jobtracker.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboards")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/seeker")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<SeekerDashboardDTO> getSeekerDashboard() {
        return ResponseEntity.ok(dashboardService.getSeekerStats());
    }

    @GetMapping("/company")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<CompanyDashboardDTO> getCompanyDashboard() {
        return ResponseEntity.ok(dashboardService.getCompanyStats());
    }
}
