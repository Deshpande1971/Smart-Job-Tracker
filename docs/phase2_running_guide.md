# Phase 2: Running & Testing Guide

Follow these steps to test the newly implemented Security and Authentication system.

## 1. Start the Application
Run the standard command in your terminal:
```powershell
./mvnw spring-boot:run
```

## 2. Test Registration (By Persona)
You should test registration for different user types to ensure the roles are stored correctly.

### A. Register as a Job Seeker
- **JSON Payload**:
```json
{
  "name": "Alex Seeker",
  "email": "alex.seeker@example.com",
  "password": "password123",
  "role": "JOB_SEEKER"
}
```

### B. Register as a Company Recruiter
- **JSON Payload**:
```json
{
  "name": "HR Manager",
  "email": "hr@google.com",
  "password": "password123",
  "role": "COMPANY"
}
```

### C. Register as an Admin
- **JSON Payload**:
```json
{
  "name": "System Admin",
  "email": "admin@smartjob.com",
  "password": "secureAdminPass",
  "role": "ADMIN"
}
```

## 3. Test Login
1. In Swagger, go to **POST /api/auth/login**.
2. Click **Try it out**.
3. Paste the credentials you just registered:
```json
{
  "email": "test@example.com",
  "password": "password123"
}
```
4. Click **Execute**. You will receive a fresh JWT token.

## 4. Verify in Database (pgAdmin)
1. Open **pgAdmin**.
2. Go to `smart_job_db` -> `Tables` -> `users`.
3. Right-click and choose **View/Edit Data**.
4. You will see your new user and their **hashed password**.

---

## 5. Troubleshooting
- **Email already exists**: If you run the registration twice with the same email, it will fail correctly.
- **Port 8080 in use**: If the app fails to start, ensure no other instance is running.
