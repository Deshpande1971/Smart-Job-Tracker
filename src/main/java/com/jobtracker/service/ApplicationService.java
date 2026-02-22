package com.jobtracker.service;

import com.jobtracker.dto.ApplicationRequestDTO;
import com.jobtracker.dto.ApplicationResponseDTO;
import com.jobtracker.dto.StatusUpdateRequestDTO;
import com.jobtracker.entity.JobApplication;
import com.jobtracker.entity.JobPosting;
import com.jobtracker.entity.User;
import com.jobtracker.exception.AlreadyExistsException;
import com.jobtracker.exception.ResourceNotFoundException;
import com.jobtracker.repository.JobApplicationRepository;
import com.jobtracker.repository.JobPostingRepository;
import com.jobtracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for handling Job Applications.
 */
@Service
@RequiredArgsConstructor
public class ApplicationService {

        private final JobApplicationRepository applicationRepository;
        private final JobPostingRepository jobPostingRepository;
        private final UserRepository userRepository;

        @Transactional
        public ApplicationResponseDTO applyToJob(UUID jobId, ApplicationRequestDTO request) {
                // 1. Get current user (Job Seeker)
                String email = SecurityContextHolder.getContext().getAuthentication().getName();
                User applicant = userRepository.findByEmail(email)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                // 2. Verify Job exists and is published
                JobPosting job = jobPostingRepository.findById(jobId)
                                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

                if (!job.isPublished()) {
                        throw new AlreadyExistsException("Cannot apply to an unpublished job");
                }

                // 3. Check if already applied
                if (applicationRepository.existsByJobIdAndApplicantId(jobId, applicant.getId())) {
                        throw new AlreadyExistsException("You have already applied for this job");
                }

                // 4. Create application
                JobApplication application = JobApplication.builder()
                                .job(job)
                                .applicant(applicant)
                                .resumeUrl(request.getResumeUrl())
                                .coverLetter(request.getCoverLetter())
                                .build();

                return mapToResponse(applicationRepository.save(application));
        }

        @Transactional
        public ApplicationResponseDTO updateStatus(UUID applicationId, StatusUpdateRequestDTO request) {
                JobApplication application = applicationRepository.findById(applicationId)
                                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

                // Authorization: Check if current user belongs to the company that posted the
                // job
                String email = SecurityContextHolder.getContext().getAuthentication().getName();
                User currentUser = userRepository.findByEmail(email)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                if (currentUser.getCompany() == null
                                || !currentUser.getCompany().getId()
                                                .equals(application.getJob().getCompany().getId())) {
                        throw new AccessDeniedException(
                                        "Unauthorized: You do not have permission to manage this application");
                }

                application.setStatus(request.getStatus());
                if (request.getNotes() != null) {
                        application.setRecruiterNotes(request.getNotes());
                }

                return mapToResponse(applicationRepository.save(application));
        }

        public List<ApplicationResponseDTO> getMyApplications() {
                String email = SecurityContextHolder.getContext().getAuthentication().getName();
                User currentUser = userRepository.findByEmail(email)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                return applicationRepository.findByApplicantId(currentUser.getId()).stream()
                                .map(this::mapToResponse)
                                .collect(Collectors.toList());
        }

        public List<ApplicationResponseDTO> getApplicationsForJob(UUID jobId) {
                JobPosting job = jobPostingRepository.findById(jobId)
                                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

                // Authorization: Check if current user belongs to the company that posted the
                // job
                String email = SecurityContextHolder.getContext().getAuthentication().getName();
                User currentUser = userRepository.findByEmail(email)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                if (currentUser.getCompany() == null
                                || !currentUser.getCompany().getId().equals(job.getCompany().getId())) {
                        throw new AccessDeniedException(
                                        "Unauthorized: You do not have permission to view these applications");
                }

                return applicationRepository.findByJobId(jobId).stream()
                                .map(this::mapToResponse)
                                .collect(Collectors.toList());
        }

        private ApplicationResponseDTO mapToResponse(JobApplication app) {
                return ApplicationResponseDTO.builder()
                                .id(app.getId())
                                .jobId(app.getJob().getId())
                                .jobTitle(app.getJob().getTitle())
                                .companyName(app.getJob().getCompany().getName())
                                .applicantId(app.getApplicant().getId())
                                .applicantName(app.getApplicant().getName())
                                .resumeUrl(app.getResumeUrl())
                                .coverLetter(app.getCoverLetter())
                                .status(app.getStatus())
                                .appliedAt(app.getAppliedAt())
                                .recruiterNotes(app.getRecruiterNotes())
                                .build();
        }
}
