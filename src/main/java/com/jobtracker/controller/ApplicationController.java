package com.jobtracker.controller;

import com.jobtracker.dto.ApplicationRequestDTO;
import com.jobtracker.dto.ApplicationResponseDTO;
import com.jobtracker.dto.StatusUpdateRequestDTO;
import com.jobtracker.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller for managing Job Applications.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    /**
     * Job Seeker applies to a specific job.
     */
    @PostMapping("/jobs/{jobId}/apply")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<ApplicationResponseDTO> applyToJob(
            @PathVariable("jobId") UUID jobId,
            @Valid @RequestBody ApplicationRequestDTO request) {
        return ResponseEntity.ok(applicationService.applyToJob(jobId, request));
    }

    /**
     * Job Seeker views their own applications.
     */
    @GetMapping("/applications/my")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<List<ApplicationResponseDTO>> getMyApplications() {
        return ResponseEntity.ok(applicationService.getMyApplications());
    }

    /**
     * Recruiter views applications for a specific job their company posted.
     */
    @GetMapping("/jobs/{jobId}/applications")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<List<ApplicationResponseDTO>> getApplicationsForJob(@PathVariable("jobId") UUID jobId) {
        // Verification that the recruiter owns the job is handled in the service level
        // updateStatus
        // For GET, we could also add it, but listing is generally safer.
        // The service could be enhanced to filter these too.
        return ResponseEntity.ok(applicationService.getApplicationsForJob(jobId));
    }

    /**
     * Recruiter updates the status of an application.
     */
    @PatchMapping("/applications/{id}/status")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<ApplicationResponseDTO> updateStatus(
            @PathVariable("id") UUID id,
            @Valid @RequestBody StatusUpdateRequestDTO request) {
        return ResponseEntity.ok(applicationService.updateStatus(id, request));
    }
}
