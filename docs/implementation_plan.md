# Smart Job Tracker - MVP Implementation Plan

This document outlines the 5-phase plan to build the Smart Job Tracker backend from scratch.

## User Review Required
> [!IMPORTANT]
> - Please provide your PostgreSQL database credentials (URL, username, password). I will guide you on where to put them or add them for you in `src/main/resources/application.properties`.
> - Do you want to use **Maven** or **Gradle** for this Spring Boot project?
> - Are you okay to start with Phase 1?

## Proposed Changes

We will build the application adhering strictly to the provided REST API requirements and the `com.jobtracker` package architecture.

### Phase 1: Initialization & Infrastructure
Setup the basic skeleton for the backend, standardizing the folder structure as per best practices.
- Generate Spring Boot app.
- Create packages: `config`, `controller`, `service`, `repository`, `entity`, `dto`, `security`, `exception`, `util`.
- Create `GlobalExceptionHandler` to handle application-wide errors gracefully.
- Configure application properties for the database connection.

### Phase 2: Security & Authentication
Add role-based (JOB_SEEKER, COMPANY, ADMIN) JWT authentication.
- Implement `User` Entity and BCrypt Password Encoding.
- Write Auth Controller and Services.

### Phase 3: Company & Core Entities
Enable companies to register their profiles and post jobs.
- Implement Company & Job Posting CRUD operations.
- Ensure proper mapping (Company 1 -> N Jobs).

### Phase 4: Job Application & Applicant Tracking
Enable candidates to apply and recruiters to track applications.
- Implement Application handling status transition logic.
- Handle constraints (e.g., job must be published to apply, cannot apply twice).

### Phase 5: Interviews, Dashboards & Deployment readiness
Finish additional functionalities and prepare for deployment.
- Add Interview scheduling endpoints.
- Gather system metrics for dashboards.
- Add Swagger/OpenAPI configuration.
- Generate standard `Dockerfile` and `docker-compose.yml`.

## Verification Plan

After each phase, we will:
1. Update `docs/user_guide.md` with instructions on how to test the newly implemented features.
2. Provide cURL/Postman snippets or direct commands to test locally.
3. Not move to the next phase without your explicit confirmation that the current one works properly and is approved.
