# Phase 3: Company & Job Management - Detailed Explanation

Phase 3 introduces the core business entities for the Smart Job Tracker: **Companies** and **Job Postings**. This phase builds a bridge between the authentication layer (Phase 2) and the application tracking system (Phase 4).

## 🏢 1. The Company Model

A `Company` in our system is more than just a name; it's the organizational unit that owns job postings.

### Key Logic:
- **Slug Generation**: Company names are automatically converted to URL-friendly "slugs" (e.g., "Tech Innovators" becomes `tech-innovators`).
- **Owner Linkage**: When a user with `ROLE_COMPANY` registers a company, that user is automatically linked to the company as its primary member. This is a critical security step for Phase 4.
- **Verification Cycle**: Companies start as `verified = false`. An `ADMIN` must verify them to grant full platform trust.

## 💼 2. The Job Posting Model

`JobPosting` captures everything required for a recruitment cycle.

### Key Logic:
- **Recruiter Authorization**: We use **Method-Level Security** (`@PreAuthorize`) and **Service-Level Validation** to ensure a recruiter only posts jobs for the company they actually belong to.
- **Data Integrity**: Job postings have mandatory fields (Title, Description) and optional metadata (Salary, Skills, Deadline) to support complex filtering in the future.
- **Publication Toggle**: `isPublished` allows recruiters to draft jobs before making them public.

## 🛡️ 3. Security Design

We implemented a custom security filter and global exception handling to manage permissions:
1. **Public vs. Protected**:
    - `GET` endpoints for `/api/jobs` and `/api/companies` are public to allow seekers to browse.
    - `POST`, `PUT`, `DELETE`, and `PATCH` are protected by JWT and role checks.
2. **Unified Error Responses**:
    - Instead of standard Spring 403 pages, our `GlobalExceptionHandler` intercepts `AccessDeniedException` and returns a JSON object with:
        - `status: 403`
        - `error: "Forbidden"`
        - `message: "Access Denied: ..."`

## 📊 4. Database Schema Changes
- **New Tables**: `companies`, `job_postings`, `job_skills` (Element Collection).
- **FK Relationships**:
    - `users.company_id` -> `companies.id`
    - `job_postings.company_id` -> `companies.id`
    - `job_skills.job_id` -> `job_postings.id`
