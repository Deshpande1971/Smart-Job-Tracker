# Phase 5 Bug Fix & Stability Report

This document details the issues identified and resolved during the final verification and deployment preparation of Phase 5 (Interviews, Dashboards & Deployment).

## 1. Security Constraint: Cross-Company Interview Scheduling
- **Issue**: Initial implementation of `InterviewService` did not strictly verify if the recruiter scheduling the interview actually owned the job application.
- **Risk**: A malicious recruiter could potentially schedule interviews for candidates applying to other companies if they knew the `applicationId`.
- **Fix**: Added an ownership check in `InterviewService.scheduleInterview`. 
- **Code Change**:
  ```java
  if (currentUser.getCompany() == null || 
      !currentUser.getCompany().getId().equals(application.getJob().getCompany().getId())) {
      throw new AccessDeniedException("Unauthorized: You do not have permission to schedule interviews for this application");
  }
  ```
- **Result**: Unauthorized scheduling attempts now correctly return a **403 Forbidden** status.

## 2. Test Data Pollution (Unique Constraint Violations)
- **Issue**: Deep verification tests were failing with `400 Bad Request` or SQL integrity errors when run repeatedly.
- **Cause**: The `Company` entity has unique constraints on `name`, `slug`, and `contactEmail`. Tests were reusing static strings like "Company A".
- **Fix**: Implemented a "Randomized Suffix" strategy in `Phase5DeepVerificationTest.setUp()`.
- **Code Change**:
  ```java
  String suffix = UUID.randomUUID().toString().substring(0, 8);
  CompanyRequestDTO compReq = CompanyRequestDTO.builder()
      .name("Company A " + suffix)
      .contactEmail("admin" + suffix + "@company.com")
      .build();
  ```
- **Result**: Tests are now 100% isolated and can run in parallel without colliding.

## 3. Invalid Interview Timestamps
- **Issue**: Recruiters could mistakenly schedule interviews in the past, leading to data inconsistency in the dashboards and notifications.
- **Fix**: Enforced JSR-303 validation on the `InterviewRequestDTO`.
- **Code Change**:
  ```java
  @NotNull @Future(message = "Start time must be in the future")
  private LocalDateTime slotStart;
  ```
- **Result**: Any attempt to schedule a past interview is blocked at the controller layer with a **400 Bad Request**.

## 4. Seeker Response Authorization
- **Issue**: A flaw was discovered where any authenticated Job Seeker could theoretically accept an interview slot belonging to another seeker.
- **Fix**: Added a participant check in `InterviewService.updateInterviewStatus`.
- **Code Change**:
  ```java
  if (currentUser.getRole() == UserRole.JOB_SEEKER) {
      if (!interview.getApplication().getApplicant().getId().equals(currentUser.getId())) {
          throw new AccessDeniedException("Unauthorized: You cannot manage this interview");
      }
  }
  ```
- **Result**: Privacy is ensured; seekers can only see and respond to their own interview invitations.

## 5. Docker Network Synchronization
- **Issue**: The backend container was starting faster than the PostgreSQL container, causing the app to crash on startup due to a failed DB connection.
- **Fix**: Improved the `docker-compose.yml` health-check mechanism.
- **Change**: Added `pg_isready` check to the database service and a `service_healthy` condition to the backend dependency.
- **Result**: The backend now waits for the database to be fully ready before attempting to boot.
