# Phase 4 TODO List: Job Application & Tracking

## 1. Domain & Data Layer
- [x] Create `ApplicationStatus` enum (APPLIED, SHORTLISTED, etc.)
- [x] Create `JobApplication` entity with relationships (`User` and `JobPosting`)
- [x] Create `JobApplicationRepository`

## 2. Data Transfer Objects (DTOs)
- [x] Create `ApplicationRequestDTO` (resumeUrl, coverLetter)
- [x] Create `ApplicationResponseDTO` (Full details)
- [x] Create `StatusUpdateRequestDTO` (newStatus, notes)

## 3. Business Logic (Services)
- [x] Implement `ApplicationService.applyToJob()`
    - Check if job is published
    - Check if already applied
    - Link to current Job Seeker
- [x] Implement `ApplicationService.updateStatus()` (Company only)
- [x] Implement retrieval logic (Seeker's applications vs Company's applicants)

## 4. API Layer (Controllers)
- [x] Create `ApplicationController`
    - `POST /api/jobs/{jobId}/apply` (Seeker only)
    - `GET /api/applications/my` (Seeker view)
    - `GET /api/jobs/{jobId}/applications` (Company view)
    - `PATCH /api/applications/{id}/status` (Company only)

## 5. Documentation & Verification
- [x] Create `docs/phase4_explanation.md`
- [x] Create `docs/phase4_endpoints.md`
- [x] Create `docs/phase4_running_guide.md`
- [x] Implement `ApplicationDeepVerificationTest.java` (Edge cases & Security)
- [x] Global Controller Refactor: Named `@PathVariable` for test stability
