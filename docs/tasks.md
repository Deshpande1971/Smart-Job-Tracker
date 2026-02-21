# MVP Implementation Phases - Tasks

## Phase 1: Initialization & Infrastructure
- [ ] Initialize Spring Boot project (Web, JPA, PostgreSQL, Security, Validation, Lombok)
- [ ] Create folder structure (`config`, `controller`, `service`, `repository`, `entity`, `dto`, `security`, `exception`, `util`)
- [ ] Configure `GlobalExceptionHandler` and base DTOs
- [ ] Set up PostgreSQL connection in properties (Waiting for user details)
- [ ] Update `docs/user_guide.md` for local setup

## Phase 2: Security & Authentication
- [ ] Create `User` entity and repository
- [ ] Implement JWT utilities (`JwtUtil`, `JwtFilter`, `CustomUserDetailsService`)
- [ ] Implement `SecurityConfig`
- [ ] Implement `AuthService` and `AuthController` (Register, Login)
- [ ] Test Auth Endpoints

## Phase 3: Company & Core Entities (Job Posting)
- [ ] Create `Company` entity and repository
- [ ] Create `JobPosting` (Job) entity and repository
- [ ] Implement `CompanyService` and `CompanyController`
- [ ] Implement `JobService` and `JobController`
- [ ] Test Company & Job Endpoints

## Phase 4: Job Application & Applicant Tracking
- [ ] Create `JobApplication` entity and repository
- [ ] Implement application workflow (`/api/jobs/{jobId}/apply`)
- [ ] Implement status update workflow for recruiters (APPLIED → SHORTLISTED → INTERVIEW_SCHEDULED → INTERVIEWED → OFFERED → REJECTED)
- [ ] Test Application Endpoints

## Phase 5: Interviews, Dashboards & Deployment readiness
- [ ] Implement Interview Scheduling endpoints
- [ ] Implement Dashboard summary endpoints
- [ ] Add Swagger configuration (`SwaggerConfig`)
- [ ] Dockerize application (Dockerfile, docker-compose)
