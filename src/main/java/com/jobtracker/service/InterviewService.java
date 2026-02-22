package com.jobtracker.service;

import com.jobtracker.dto.InterviewRequestDTO;
import com.jobtracker.dto.InterviewResponseDTO;
import com.jobtracker.entity.*;
import com.jobtracker.exception.ResourceNotFoundException;
import com.jobtracker.repository.InterviewRepository;
import com.jobtracker.repository.JobApplicationRepository;
import com.jobtracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InterviewService {

    private final InterviewRepository interviewRepository;
    private final JobApplicationRepository applicationRepository;
    private final UserRepository userRepository;

    @Transactional
    public InterviewResponseDTO scheduleInterview(UUID applicationId, InterviewRequestDTO request) {
        // 1. Find the application we want to schedule for
        JobApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        // 2. Identify the person currently logged in
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // 3. Security: Ensure this recruiter works for the company that posted the job
        if (currentUser.getCompany() == null ||
                !currentUser.getCompany().getId().equals(application.getJob().getCompany().getId())) {
            throw new AccessDeniedException(
                    "Unauthorized: You do not have permission to schedule interviews for this application");
        }

        // 4. Create the interview record
        Interview interview = Interview.builder()
                .application(application)
                .slotStart(request.getSlotStart())
                .slotEnd(request.getSlotEnd())
                .mode(request.getMode())
                .locationOrLink(request.getLocationOrLink())
                .build();

        // 5. Automatically update application status if not already interview scheduled
        if (application.getStatus() != ApplicationStatus.INTERVIEW_SCHEDULED) {
            application.setStatus(ApplicationStatus.INTERVIEW_SCHEDULED);
            applicationRepository.save(application);
        }

        // 6. Save everything to the database
        return mapToResponse(interviewRepository.save(interview));
    }

    @Transactional
    public InterviewResponseDTO updateInterviewStatus(UUID interviewId, InterviewStatus status) {
        // 1. Find the interview
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found"));

        // 2. Identify current user
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // 3. Security check: Seeker can accept/reject, Company can cancel/complete
        if (currentUser.getRole() == UserRole.JOB_SEEKER) {
            // Ensure the seeker is responding to their OWN interview
            if (!interview.getApplication().getApplicant().getId().equals(currentUser.getId())) {
                throw new AccessDeniedException("Unauthorized: You cannot manage this interview");
            }
            if (status != InterviewStatus.ACCEPTED && status != InterviewStatus.REJECTED) {
                throw new RuntimeException("Seekers can only ACCEPT or REJECT interviews");
            }
        } else if (currentUser.getRole() == UserRole.COMPANY) {
            // Ensure the recruiter works for the company that owns this interview
            if (!interview.getApplication().getJob().getCompany().getId().equals(currentUser.getCompany().getId())) {
                throw new AccessDeniedException("Unauthorized: You cannot manage this interview");
            }
        }

        // 4. Update and Save
        interview.setStatus(status);
        return mapToResponse(interviewRepository.save(interview));
    }

    public List<InterviewResponseDTO> getInterviewsByApplication(UUID applicationId) {
        // Find all interview rounds for a specific application
        return interviewRepository.findByApplicationId(applicationId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private InterviewResponseDTO mapToResponse(Interview interview) {
        // Convert the database entity into a clean DTO for the frontend
        return InterviewResponseDTO.builder()
                .id(interview.getId())
                .applicationId(interview.getApplication().getId())
                .jobTitle(interview.getApplication().getJob().getTitle())
                .companyName(interview.getApplication().getJob().getCompany().getName())
                .slotStart(interview.getSlotStart())
                .slotEnd(interview.getSlotEnd())
                .mode(interview.getMode())
                .locationOrLink(interview.getLocationOrLink())
                .status(interview.getStatus())
                .build();
    }
}
