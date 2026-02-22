package com.jobtracker.controller;

import com.jobtracker.dto.JobRequestDTO;
import com.jobtracker.dto.JobResponseDTO;
import com.jobtracker.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for Job Posting management.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    // Recruiters post jobs under their company ID.
    @PostMapping("/companies/{companyId}/jobs")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<JobResponseDTO> createJob(
            @PathVariable("companyId") UUID companyId,
            @Valid @RequestBody JobRequestDTO request) {
        return ResponseEntity.ok(jobService.createJob(companyId, request));
    }

    // Public endpoint for searching jobs.
    @GetMapping("/jobs")
    public ResponseEntity<List<JobResponseDTO>> getAllPublishedJobs() {
        return ResponseEntity.ok(jobService.getAllPublishedJobs());
    }

    // Get jobs for a specific company.
    @GetMapping("/companies/{companyId}/jobs")
    public ResponseEntity<List<JobResponseDTO>> getJobsByCompany(@PathVariable("companyId") UUID companyId) {
        return ResponseEntity.ok(jobService.getJobsByCompany(companyId));
    }
}
