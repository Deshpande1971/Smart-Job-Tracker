package com.jobtracker.controller;

import com.jobtracker.dto.InterviewRequestDTO;
import com.jobtracker.dto.InterviewResponseDTO;
import com.jobtracker.entity.InterviewStatus;
import com.jobtracker.service.InterviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;

    @PostMapping("/applications/{applicationId}/interviews")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<InterviewResponseDTO> scheduleInterview(
            @PathVariable("applicationId") UUID applicationId,
            @Valid @RequestBody InterviewRequestDTO request) {
        return ResponseEntity.ok(interviewService.scheduleInterview(applicationId, request));
    }

    @PatchMapping("/interviews/{id}/status")
    @PreAuthorize("hasAnyRole('COMPANY', 'JOB_SEEKER')")
    public ResponseEntity<InterviewResponseDTO> updateInterviewStatus(
            @PathVariable("id") UUID id,
            @RequestParam("status") InterviewStatus status) {
        return ResponseEntity.ok(interviewService.updateInterviewStatus(id, status));
    }

    @GetMapping("/applications/{applicationId}/interviews")
    public ResponseEntity<List<InterviewResponseDTO>> getInterviewsByApplication(
            @PathVariable("applicationId") UUID applicationId) {
        return ResponseEntity.ok(interviewService.getInterviewsByApplication(applicationId));
    }
}
