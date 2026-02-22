# Phase 4: Running & Verification Guide

## 🛠️ Automated Verification

Run the automated test suite to verify the application flow and cross-company security:

```powershell
./mvnw test -Dtest=ApplicationVerificationTest
```

---

## 🧪 Manual Verification personae

### Persona 1: The Job Seeker (Applying)
1. **Login** as a user with `role: JOB_SEEKER`.
2. **Apply** to an existing job: `POST /api/jobs/{jobId}/apply`.
3. **Check My Applications**: `GET /api/applications/my`.

### Persona 2: The Recruiter (Managing)
1. **Login** as a user with `role: COMPANY`.
2. **View Applicants**: `GET /api/jobs/{jobId}/applications`.
3. **Shortlist Candidate**: `PATCH /api/applications/{id}/status` with `{ "status": "SHORTLISTED" }`.

---

## 📍 Postman / cURL Quickstart

### Apply to Job
```bash
curl -X POST http://localhost:8080/api/jobs/{jobId}/apply \
-H "Authorization: Bearer <SEEKER_TOKEN>" \
-H "Content-Type: application/json" \
-d '{
  "resumeUrl": "http://example.com/resume.pdf"
}'
```

### Update Status
```bash
curl -X PATCH http://localhost:8080/api/applications/{applicationId}/status \
-H "Authorization: Bearer <RECRUITER_TOKEN>" \
-H "Content-Type: application/json" \
-d '{
  "status": "OFFERED",
  "notes": "Candidate was exceptional."
}'
```
