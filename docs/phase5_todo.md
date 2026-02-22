# Phase 5 TODO List: Interviews, Dashboards & Deployment

## 1. Domain & Data Layer (Interviews)
- [x] Create `InterviewMode` enum (VIDEO, IN_PERSON, TELEPHONY)
- [x] Create `InterviewStatus` enum (PENDING, ACCEPTED, REJECTED, COMPLETED, CANCELLED)
- [x] Create `Interview` entity with relationships to `JobApplication`
- [x] Create `InterviewRepository`

## 2. Data Transfer Objects (DTOs)
- [x] Create `InterviewRequestDTO`
- [x] Create `InterviewResponseDTO`
- [x] Create `SeekerDashboardDTO`
- [x] Create `CompanyDashboardDTO`

## 3. Business Logic (Services)
- [x] Implement `InterviewService`
    - Logic for scheduling (Company only)
    - Logic for responding (Seeker only)
- [x] Implement `DashboardService`
    - Aggregation logic for seeker stats (Apps, Interviews, Offers, Rejections)
    - Aggregation logic for company stats (Jobs, Total Applicants, Status breakdown)

## 4. API Layer (Controllers)
- [x] Create `InterviewController`
- [x] Create `DashboardController`

## 5. Infrastructure & Deployment
- [x] Add Swagger/OpenAPI dependency
- [x] Configure Swagger UI access in `SecurityConfig`
- [x] Create `Dockerfile`
- [x] Create `docker-compose.yml`

## 6. Documentation & Verification
- [x] Create `docs/phase5_explanation.md`
- [x] Create `docs/phase5_endpoints.md`
- [x] Create `docs/phase5_running_guide.md`
- [x] Implement `InterviewVerificationTest.java`
- [x] Implement `DashboardVerificationTest.java`
