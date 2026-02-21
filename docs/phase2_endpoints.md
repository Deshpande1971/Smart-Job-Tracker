# Phase 2: Security & Authentication Endpoints

This document lists the new endpoints introduced in Phase 2.

## 1. Authentication Endpoints

| Endpoint | Method | Access | Description |
| :--- | :--- | :--- | :--- |
| `/api/auth/register` | POST | **Public** | Register a new user (Seeker or Company). |
| `/api/auth/login` | POST | **Public** | Authenticate user and receive a JWT token. |

### Registration Request (`POST /api/auth/register`)
**JSON Payload:**
```json
{
  "name": "John Doe",
  "email": "john.doe@example.com",
  "password": "password123",
  "role": "JOB_SEEKER"
}
```
*Roles can be `JOB_SEEKER` or `COMPANY`.*

### Login Request (`POST /api/auth/login`)
**JSON Payload:**
```json
{
  "email": "john.doe@example.com",
  "password": "password123"
}
```

---

## 2. Using the JWT Token
After a successful login, you will receive a token. To access protected endpoints (coming in Phase 3+):
1.  Copy the `token` string.
2.  Add it to your HTTP headers as: 
    *   **Key**: `Authorization`
    *   **Value**: `Bearer [YOUR_TOKEN]`

---

## 3. Swagger UI
All these endpoints are now interactive in Swagger:
`http://localhost:8080/swagger-ui.html`
Look for the **Auth Controller** section.
