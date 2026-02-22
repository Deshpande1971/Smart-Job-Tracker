package com.jobtracker.service;

import com.jobtracker.dto.JobRequestDTO;
import com.jobtracker.dto.JobResponseDTO;
import com.jobtracker.entity.Company;
import com.jobtracker.entity.JobPosting;
import com.jobtracker.entity.User;
import com.jobtracker.repository.CompanyRepository;
import com.jobtracker.repository.JobPostingRepository;
import com.jobtracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Handles Job Posting logic for recruiters and public searching.
 */
@Service
@RequiredArgsConstructor
public class JobService {

    private final JobPostingRepository jobPostingRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    @Transactional
    public JobResponseDTO createJob(UUID companyId, JobRequestDTO request) {
        // 1. Verify Company exists
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        // 2. Authorization: Check if current user belongs to this company
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (currentUser.getCompany() == null || !currentUser.getCompany().getId().equals(companyId)) {
            throw new RuntimeException("Unauthorized: You do not belong to this company");
        }

        // 3. Map to Entity
        JobPosting job = JobPosting.builder()
                .company(company)
                .title(request.getTitle())
                .description(request.getDescription())
                .location(request.getLocation())
                .employmentType(request.getEmploymentType())
                .experienceLevel(request.getExperienceLevel())
                .skills(request.getSkills())
                .salaryMin(request.getSalaryMin())
                .salaryMax(request.getSalaryMax())
                .applicationDeadline(request.getApplicationDeadline())
                .isPublished(request.isPublished())
                .build();

        return mapToResponse(jobPostingRepository.save(job));
    }

    @Transactional(readOnly = true)
    public List<JobResponseDTO> getAllPublishedJobs() {
        return jobPostingRepository.findByIsPublishedTrue().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<JobResponseDTO> getJobsByCompany(UUID companyId) {
        return jobPostingRepository.findByCompanyId(companyId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private JobResponseDTO mapToResponse(JobPosting job) {
        return JobResponseDTO.builder()
                .id(job.getId())
                .companyId(job.getCompany().getId())
                .companyName(job.getCompany().getName())
                .title(job.getTitle())
                .description(job.getDescription())
                .location(job.getLocation())
                .employmentType(job.getEmploymentType())
                .experienceLevel(job.getExperienceLevel())
                .skills(job.getSkills() != null ? new ArrayList<>(job.getSkills()) : new ArrayList<>())
                .salaryMin(job.getSalaryMin())
                .salaryMax(job.getSalaryMax())
                .applicationDeadline(job.getApplicationDeadline())
                .createdAt(job.getCreatedAt())
                .build();
    }
}
