package com.jobtracker.controller;

import com.jobtracker.entity.User;
import com.jobtracker.entity.UserRole;
import com.jobtracker.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dev")
@RequiredArgsConstructor
public class DevToolsController {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final JobPostingRepository jobPostingRepository;
    private final JobApplicationRepository applicationRepository;
    private final InterviewRepository interviewRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/reset-db")
    public ResponseEntity<String> resetDatabase() {
        // 1. Wipe everything (Order matters due to Foreign Keys)
        interviewRepository.deleteAll();
        applicationRepository.deleteAll();
        jobPostingRepository.deleteAll();

        // Remove company links from users first
        userRepository.findAll().forEach(user -> {
            user.setCompany(null);
            userRepository.save(user);
        });

        companyRepository.deleteAll();
        userRepository.deleteAll();

        // 2. Re-seed basic Admin and test users
        User admin = User.builder()
                .name("System Admin")
                .email("admin@jobtracker.com")
                .password(passwordEncoder.encode("admin123"))
                .role(UserRole.ADMIN)
                .build();
        userRepository.save(admin);

        User testRecruiter = User.builder()
                .name("Test Recruiter")
                .email("recruiter@test.com")
                .password(passwordEncoder.encode("pass123"))
                .role(UserRole.COMPANY)
                .build();
        userRepository.save(testRecruiter);

        User testSeeker = User.builder()
                .name("Test Seeker")
                .email("seeker@test.com")
                .password(passwordEncoder.encode("pass123"))
                .role(UserRole.JOB_SEEKER)
                .build();
        userRepository.save(testSeeker);

        return ResponseEntity.ok(
                "Database has been reset. Use admin@jobtracker.com / recruiter@test.com / seeker@test.com to login.");
    }
}
