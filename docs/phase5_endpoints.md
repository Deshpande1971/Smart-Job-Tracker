# Phase 5: API Reference Guide - Interviews & Dashboards

## 📅 Interview Endpoints

### 1. Schedule Interview
`POST /api/applications/{applicationId}/interviews`
- **Role**: `COMPANY`
- **Request Body**:
```json
{
  "slotStart": "2026-03-01T10:00:00",
  "slotEnd": "2026-03-01T11:00:00",
  "mode": "VIDEO",
  "locationOrLink": "https://zoom.us/j/meeting-id"
}
```

### 2. Update Interview Status (Accept/Reject)
`PATCH /api/interviews/{id}/status?status=ACCEPTED`
- **Role**: `JOB_SEEKER` or `COMPANY`
- **Params**: `status` (ACCEPTED, REJECTED, COMPLETED, CANCELLED).
- **Security**: Seekers can only flip to ACCEPTED/REJECTED for their own interviews.

### 3. Get Interviews for Application
`GET /api/applications/{applicationId}/interviews`
- **Role**: Any authorized user.

---

## 📊 Dashboard Endpoints

### 1. Seeker Dashboard
`GET /api/dashboards/seeker`
- **Role**: `JOB_SEEKER`
- **Returns**: Stats on total apps, interviews, offers, and rejections.

### 2. Company Dashboard
`GET /api/dashboards/company`
- **Role**: `COMPANY`
- **Returns**: Stats on total jobs posted and total applicant funnel.

---

## 🏗️ Technical Endpoints

### Swagger UI
`GET http://localhost:8080/swagger-ui.html`
- **Role**: Public
- **Description**: Interactive API documentation.
