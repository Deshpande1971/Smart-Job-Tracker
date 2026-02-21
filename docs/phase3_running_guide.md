# Phase 3: Running & Verification Guide

This guide describes how to run and manually verify the Phase 3 features using the provided automated tests and Postman/cURL.

## 🛠️ Automated Verification (Recommended)

Run the comprehensive test suite to verify all business logic, role-based security, and edge cases.

```powershell
# Run the basic flow test
./mvnw test -Dtest=CompanyJobVerificationTest

# Run the complex edge-case and security test 
./mvnw test -Dtest=CompanyJobEdgeCaseTest
```

---

## 🧪 Manual Verification Personas

### Persona 1: The Recruiter (Company Registration)
1. **Register** a new account with `role: "COMPANY"`.
2. **Login** to get your JWT Token.
3. **Try to Post a Job immediately**:
    - Perform `POST /api/companies/{ANY_UUID}/jobs`.
    - **Expected**: `500` or `403` error with message "You do not belong to this company".
4. **Register your Company**:
    - Perform `POST /api/companies`.
    - **Expected**: `200 OK`. Note the `id` from the response.
5. **Post a Job correctly**:
    - Perform `POST /api/companies/{YOUR_COMPANY_ID}/jobs`.
    - **Expected**: `200 OK`.

### Persona 2: The Job Seeker (Browsing)
1. **Register** a new account with `role: "JOB_SEEKER"`.
2. **Browse Jobs**:
    - Perform `GET /api/jobs` (No token required).
3. **Try to Register a Company**:
    - Perform `POST /api/companies` using your seeker token.
    - **Expected**: `403 Forbidden` JSON response.

---

## 📍 Postman / cURL Quickstart

### Create Company
```bash
curl -X POST http://localhost:8080/api/companies \
-H "Authorization: Bearer <TOKEN>" \
-H "Content-Type: application/json" \
-d '{
  "name": "Alpha Systems",
  "description": "Building sustainable systems for the modern era.",
  "industry": "Consulting",
  "contactEmail": "hr@alphasys.test"
}'
```

---

## 🐞 Troubleshooting
- **Database Schema Sync**: If you see "Table not found", check `application.properties` for `spring.jpa.hibernate.ddl-auto=update`.
- **Validation Error**: If you receive a 400 response, ensure your description is at least 20 characters long.
