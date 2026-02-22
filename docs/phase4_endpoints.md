# Phase 4: API Reference Guide - Job Applications

All endpoints require an `Authorization: Bearer <TOKEN>` header.

## 📝 Job Seeker Endpoints

### 1. Apply for a Job
`POST /api/jobs/{jobId}/apply`
- **Role**: `JOB_SEEKER`
- **Request Body**:
```json
{
  "resumeUrl": "https://storage.provider.com/my-resume.pdf",
  "coverLetter": "Hello, I am interested in this role..."
}
```
- **Constraint**: Cannot apply if `job.isPublished = false` or if already applied.

### 2. View My Applications
`GET /api/applications/my`
- **Role**: `JOB_SEEKER`
- **Returns**: A list of all applications submitted by the current user.

---

## 🏢 Company Endpoints

### 1. View Applicants for a Job
`GET /api/jobs/{jobId}/applications`
- **Role**: `COMPANY`
- **Returns**: List of all applicants for the specified job.

### 2. Update Application Status
`PATCH /api/applications/{id}/status`
- **Role**: `COMPANY`
- **Request Body**:
```json
{
  "status": "SHORTLISTED",
  "notes": "Good match, let's setup a call."
}
```
- **Valid Statuses**: `APPLIED`, `SHORTLISTED`, `INTERVIEW_SCHEDULED`, `INTERVIEWED`, `OFFERED`, `REJECTED`.

---

## ⚠️ Error Reference

| Status | Code | Scenario |
| :--- | :--- | :--- |
| **400** | `Bad Request` | Already applied for this job. |
| **403** | `Forbidden` | Recruiter trying to manage an application for a different company. |
| **404** | `Not Found` | Invalid Job or Application UUID. |
