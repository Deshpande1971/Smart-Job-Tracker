package com.jobtracker.service;

import com.jobtracker.dto.CompanyDashboardDTO;
import com.jobtracker.dto.SeekerDashboardDTO;
import com.jobtracker.entity.ApplicationStatus;
import com.jobtracker.entity.JobApplication;
import com.jobtracker.entity.JobPosting;
import com.jobtracker.entity.User;
import com.jobtracker.exception.ResourceNotFoundException;
import com.jobtracker.repository.JobApplicationRepository;
import com.jobtracker.repository.JobPostingRepository;
import com.jobtracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

        private final JobApplicationRepository applicationRepository;
        private final JobPostingRepository jobPostingRepository;
        private final UserRepository userRepository;

        public SeekerDashboardDTO getSeekerStats() {
                // 1. Identify current logged-in user
                String email = SecurityContextHolder.getContext().getAuthentication().getName();
                User currentUser = userRepository.findByEmail(email)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                // 2. Fetch all their applications
                List<JobApplication> applications = applicationRepository.findByApplicantId(currentUser.getId());

                // 3. Group and count applications by their status (e.g., "OFFERED": 2)
                Map<String, Long> statusBreakdown = applications.stream()
                                .collect(Collectors.groupingBy(app -> app.getStatus().name(), Collectors.counting()));

                // 4. Package everything into a summary object
                return SeekerDashboardDTO.builder()
                                .totalApplications(applications.size())
                                .interviewCount(statusBreakdown
                                                .getOrDefault(ApplicationStatus.INTERVIEW_SCHEDULED.name(), 0L) +
                                                statusBreakdown.getOrDefault(ApplicationStatus.INTERVIEWED.name(), 0L))
                                .offerCount(statusBreakdown.getOrDefault(ApplicationStatus.OFFERED.name(), 0L))
                                .rejectedCount(statusBreakdown.getOrDefault(ApplicationStatus.REJECTED.name(), 0L))
                                .statusBreakdown(statusBreakdown)
                                .build();
        }

        public CompanyDashboardDTO getCompanyStats() {
                String email = SecurityContextHolder.getContext().getAuthentication().getName();
                User currentUser = userRepository.findByEmail(email)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                if (currentUser.getCompany() == null) {
                        throw new RuntimeException("User not associated with a company");
                }

                List<JobPosting> jobs = jobPostingRepository.findByCompanyId(currentUser.getCompany().getId());
                List<JobApplication> applications = applicationRepository
                                .findByJobCompanyId(currentUser.getCompany().getId());

                Map<String, Long> statusBreakdown = applications.stream()
                                .collect(Collectors.groupingBy(app -> app.getStatus().name(), Collectors.counting()));

                return CompanyDashboardDTO.builder()
                                .totalJobsPosted(jobs.size())
                                .totalApplicants(applications.size())
                                .applicantsByStatus(statusBreakdown)
                                .build();
        }
}
