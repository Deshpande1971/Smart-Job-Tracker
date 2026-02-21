# Create the complete MVP requirements markdown content
mvp_content = """
# Smart Job Tracker – Full Stack MVP Requirements

## Project Overview

Smart Job Tracker is a full-stack job management platform that connects **Job Seekers** and **Companies**.

The system consists of:

- Spring Boot Backend (REST API)
- PostgreSQL Database
- React Frontend
- Dockerized deployment
- Role-based authentication and authorization

---

# System Architecture

Frontend (React)
        ↓ REST API
Backend (Spring Boot)
        ↓
PostgreSQL Database

Deployment:
- Backend → Docker + Cloud (AWS/Render)
- Frontend → Vercel/Netlify

---

# Roles

## 1. JOB_SEEKER
- Register & login
- Maintain profile
- Apply to jobs
- Track application status
- View interview schedule

## 2. COMPANY
- Register company
- Create recruiter accounts
- Post job vacancies
- View applicants
- Update application status
- Schedule interviews

## 3. ADMIN
- Verify companies
- Suspend companies
- View system metrics

---

# Backend MVP Requirements

## Authentication

### Register
POST /api/auth/register

Fields:
- name (required)
- email (unique, required)
- password (encrypted using BCrypt)
- role (JOB_SEEKER or COMPANY)

### Login
POST /api/auth/login

Returns JWT token with expiration.

---

## Company Management

### Create Company
POST /api/companies

Required Fields:
- name
- description (min 20 chars)
- industry
- contactEmail

Optional:
- website
- headquarters
- companySize
- logoUrl

Company default:
- verified = false
- status = ACTIVE

---

## Job Posting (Company)

### Create Job
POST /api/companies/{companyId}/jobs

Fields:
- title (required)
- description (required)
- location
- employmentType
- experienceLevel
- skills (list)
- salaryMin
- salaryMax
- applicationDeadline
- isPublished (boolean)

---

## Apply to Job (Job Seeker)

POST /api/jobs/{jobId}/apply

Required:
- resume (PDF/DOCX)
- optional coverLetter

Rules:
- Cannot apply twice
- Job must be published
- Status default = APPLIED

---

## Application Status Flow

APPLIED → SHORTLISTED → INTERVIEW_SCHEDULED → INTERVIEWED → OFFERED → REJECTED

Only Company can update status.

---

## Interview Scheduling

POST /api/applications/{applicationId}/interviews

Fields:
- slotStart
- slotEnd
- mode (VIDEO / IN_PERSON / TELEPHONY)
- locationOrLink

Applicant can accept or reject.

---

## Dashboard APIs

### Job Seeker Dashboard
- Total applications
- Interview count
- Offer count
- Rejected count

### Company Dashboard
- Total jobs posted
- Total applicants
- Applicants by status

---

# Database Entities

## User
- id (UUID)
- name
- email
- password
- role
- createdAt

## Company
- id
- name
- slug
- description
- industry
- companySize
- headquarters
- contactEmail
- verified
- status

## JobPosting
- id
- companyId
- title
- description
- location
- employmentType
- experienceLevel
- skills
- salaryMin
- salaryMax
- isPublished
- createdAt

## JobApplication
- id
- jobId
- applicantId
- resumeUrl
- status
- appliedAt
- interviewDateTime
- recruiterNotes
- withdrawn

---

# Security Requirements

- JWT authentication
- Role-based access control
- BCrypt password encryption
- Input validation using @Valid
- Global exception handling
- Proper HTTP status codes

---

# Non-Functional Requirements

- Pagination & sorting
- Logging using SLF4J
- Swagger documentation
- Dockerized setup
- Environment-based configuration
- Unique constraints and indexing

---

# Frontend MVP Requirements (React)

## Pages

1. Login
2. Register
3. Dashboard
4. Job Listings
5. Company Dashboard
6. Applicant Management
7. Interview Scheduling

---

## Frontend Features

- JWT storage
- Axios interceptors
- Protected routes
- Pagination UI
- Status update dropdown
- Form validation
- Responsive UI

---

# Deployment Requirements

Backend:
- Dockerfile
- docker-compose (PostgreSQL + backend)
- Environment variables

Frontend:
- Build optimized production bundle
- Deploy to Vercel/Netlify

---

# Production-Ready Checklist

Backend:
- Authentication working
- Role-based authorization
- CRUD for jobs
- Apply flow working
- Status update flow working
- Interview scheduling implemented
- Swagger working
- Docker working

Frontend:
- Login/Register functional
- Dashboard working
- Apply to job functional
- Company can manage applicants
- Protected routes working

---

# Resume Description Example

Built a full-stack Job Application Tracking Platform using Spring Boot (JWT authentication, PostgreSQL, Dockerized backend) and React frontend with role-based access control, interview scheduling, and secure applicant management. Deployed on cloud infrastructure with production-ready architecture.
"""

# Save as markdown file
file_path = "/mnt/data/Smart_Job_Tracker_MVP_Requirements.md"
with open(file_path, "w", encoding="utf-8") as f:
    f.write(mvp_content)

file_path
