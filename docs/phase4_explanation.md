# Phase 4: Job Application & Tracking - Detailed Explanation

Phase 4 implements the interaction layer where Job Seekers apply for positions and Companies manage the candidate lifecycle.

## 📝 1. The Application Model

A `JobApplication` connects a `User` (Seeker) to a `JobPosting`. 

### Key Features:
- **Application Integrity**: 
    - A seeker cannot apply to the same job twice (enforced by a composite unique constraint in the database).
    - Applications are only allowed for published jobs.
- **Dynamic Status Lifecycle**: 
    - Default status is `APPLIED`.
    - Recruiter-driven transitions: `SHORTLISTED`, `INTERVIEW_SCHEDULED`, `INTERVIEWED`, `OFFERED`, `REJECTED`.
- **Engagement**: Fields for `resumeUrl` and `coverLetter` allow seekers to present their candidacy.

## 🛡️ 2. Security & Authorization

### Seeker Security:
- Restricted applying logic to `ROLE_JOB_SEEKER`.
- Seekers can only view *their own* applications (`/api/applications/my`).

### Recruiter Security:
- Restricted management logic to `ROLE_COMPANY`.
- **Ownership Check**: Before a recruiter can update an application's status or see private details, the system verifies that the recruiter belongs to the company that originally posted the job.

## 🏗️ 3. Database Architecture
- **Table**: `job_applications`
- **Relationships**: 
    - `job_id` -> `job_postings.id` (Many-to-One)
    - `applicant_id` -> `users.id` (Many-to-One)
- **Status Persistence**: Stored as a string enum for readability and flexibility.
