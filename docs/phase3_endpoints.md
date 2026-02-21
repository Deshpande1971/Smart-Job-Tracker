# Phase 3: API Reference Guide

All protected endpoints require an `Authorization: Bearer <JWT_TOKEN>` header.

## 🏢 Company Endpoints

### 1. Register a Company
`POST /api/companies`
- **Role**: `COMPANY`
- **Validation**: Description must be > 20 chars. Email/Name must be unique.
- **Request**:
```json
{
  "name": "Global Tech",
  "description": "Global leaders in sustainable energy solutions.",
  "industry": "CleanEnergy",
  "contactEmail": "hr@globaltech.com"
}
```

### 2. List All Companies (Public)
`GET /api/companies`

### 3. Get Company Details (Public)
`GET /api/companies/{id}`

### 4. Verify a Company (Admin Only)
`PATCH /api/companies/{id}/verify`
- **Role**: `ADMIN`

---

## 💼 Job Posting Endpoints

### 1. Create a Job
`POST /api/companies/{companyId}/jobs`
- **Role**: `COMPANY` (Must belong to the companyId provided)
- **Request**:
```json
{
  "title": "React Frontend Architect",
  "description": "Expert-level React and Redux knowledge required.",
  "location": "Remote",
  "employmentType": "FULL_TIME",
  "skills": ["React", "TypeScript", "Tailwind"],
  "salaryMin": 100000,
  "salaryMax": 160000,
  "applicationDeadline": "2026-10-15T00:00:00"
}
```

### 2. Job Board (Public)
`GET /api/jobs`
- **Behavior**: Only returns jobs where `isPublished = true`.

### 3. Company specific Jobs
`GET /api/companies/{companyId}/jobs`

---

## ⚠️ Common Error Codes

| Status | Code | Scenario |
| :--- | :--- | :--- |
| **403** | `Forbidden` | User has a valid token but wrong Role (e.g., Job Seeker trying to post a job). |
| **401** | `Unauthorized` | Token is invalid, expired, or missing. |
| **400** | `Bad Request` | Validation failure (e.g., description too short, invalid email format). |
| **404** | `Not Found` | Invalid UUID provided for Company or Job. |
