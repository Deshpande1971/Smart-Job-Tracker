# Smart Job Tracker: Detailed Operational Manual

Welcome to the official manual for the Smart Job Tracker. This guide walks you through the entire ecosystem using three main characters:
- **Aditya**: The ambitious Job Seeker looking for a "Spring Boot Developer" role.
- **Basu Patil**: The Recruiter who owns "**Window Tec Solutions**".
- **Ankit**: The Platform Administrator who has oversight over the entire system.

---

## 🛠️ Phase 0: The Clean Slate (Dev Only)
Before starting your test, ensure you have a clean database. 
- **Endpoint**: `POST http://localhost:8080/api/dev/reset-db`
- **Action**: Call this in Postman. It wipes all old data and creates fresh test accounts for you.

---

## 🧠 Phase 0.5: Crash Course on "Tokens" and "Headers" (Must Read!)

If you are new to backend testing with Postman, you might be wondering: *"What do I do with the token after I login? What is a Header?"*

*   **What is a Token?** Think of a `Token` like an electronic VIP wristband at a club. When you register, you give the bouncer your details. When you **Login** (give your email/password), the bouncer gives you a VIP wristband (the `Token`). 
    *   For every room you visit *after* that (like creating a company or posting a job), you don't show your password again; you just flash the wristband!
*   **What is a Header?** A Header is the invisible envelope that carries information along with your API request. When the manual says `Headers: Authorization: Bearer <BASU_TOKEN>`, here is exactly what you do in **Postman**:
    1.  Go to the **"Headers"** tab under the URL bar.
    2.  Under the **"Key"** column, type the word exactly as: `Authorization`
    3.  Under the **"Value"** column, type the word `Bearer`, hit the **spacebar once**, and then paste the huge token you copied from your login screen. (e.g., `Bearer eyJhbGciOiJIUzI...`)
    4.  Send the request! The server will now see your VIP wristband and let you in.

*   **Wait, I am using Swagger UI, I don't see Headers!**
    *   No problem! We just activated the Security module for Swagger. 
    *   At the top right of your Swagger page (`http://localhost:8080/swagger-ui.html`), you will now see a green **"Authorize"** button.
    *   Click it. In the `Value` box, type your huge token (you **DO NOT** need to type the word `Bearer ` here, Swagger does it for you!). Click Authorize. Now, every API call you make in Swagger will automatically include your VIP wristband.

---

## 🏢 Sequence 1: Basu Patil Sets Up Shop (Recruiter Flow)

**Basu Patil** needs to create a company and post a job before Aditya can apply.

### 1.1 Basu Registers as a Recruiter
- **Endpoint**: `POST /api/auth/register`
- **Payload**:
  ```json
  {
    "name": "Basu Patil",
    "email": "basu@windowtec.com",
    "password": "password123",
    "role": "COMPANY"
  }
  ```
- **Next Step**: Basu must **Login** via `POST /api/auth/login` to get his `Bearer Token`. Every subsequent request for Basu must include `Authorization: Bearer <BASU_TOKEN>`.

### 1.2 Basu Creates the Company Profile
- **Endpoint**: `POST /api/companies`
- **Headers**: `Authorization: Bearer <BASU_TOKEN>`
- **Payload**:
  ```json
  {
    "name": "Window Tec Solutions",
    "description": "A cutting-edge software development firm based in Bangalore, specializing in Java and Spring Boot.",
    "industry": "IT Services",
    "contactEmail": "contact@windowtec.com",
    "companySize": "51-200",
    "headquarters": "Bangalore, India",
    "website": "https://windowtec.com"
  ```
- **Troubleshooting a 403 Forbidden Error**: If you get a "403 Forbidden" here, it means your token is invalid or you are NOT logged in as a `COMPANY`.
  1. Did you run `/api/auth/login` as Basu and copy the **new** token?
  2. Did you include `Bearer ` (with a space) before the token in the Authorization header?
  3. Are you sure you didn't accidentally use Aditya's token?
- **Edge Case Error**: Try sending a description shorter than 20 characters. Basu will receive a `400 Bad Request`.

### 1.3 Basu Posts a Job
- **Action**: Basu uses the `id` from the company he just created.
- **Endpoint**: `POST /api/companies/{companyId}/jobs`
- **Headers**: `Authorization: Bearer <BASU_TOKEN>`
- **Payload**:
  ```json
  {
    "title": "Spring Boot Developer",
    "description": "Looking for a Spring Boot expert to lead our backend team.",
    "location": "Remote",
    "employmentType": "FULL_TIME",
    "published": true
  }
  ```

---

## 🔍 Sequence 2: Aditya Finds His Dream Job (Seeker Flow)

Now that a job exists, **Aditya** enters the scene.

### 2.1 Aditya Registers as a Seeker
- **Endpoint**: `POST /api/auth/register`
- **Payload**:
  ```json
  {
    "name": "Aditya",
    "email": "aditya@seeker.com",
    "password": "password123",
    "role": "JOB_SEEKER"
  }
  ```
- **Next Step**: Aditya must **Login** via `POST /api/auth/login` to get his `Bearer Token`.

### 2.2 Aditya Browses Jobs
- **Endpoint**: `GET /api/jobs`
- **Knowledge**: Aditya doesn't need to be logged in to simply browse the list, but he needs to login to apply.

### 2.3 Aditya Applies for the Job
- **Action**: Aditya uses his token to apply for Basu's "Spring Boot Developer" role.
- **Endpoint**: `POST /api/jobs/{jobId}/apply`
- **Headers**: `Authorization: Bearer <ADITYA_TOKEN>`
- **Payload**:
  ```json
  {
    "resumeUrl": "https://aditya-portfolio.com/resume.pdf",
    "coverLetter": "Hello Basu, I have 5 years of Java Spring Boot experience and would love to join your team at Window Tec."
  }
  ```
- **How it works**: This creates a `JobApplication` record with status `APPLIED`.

---

## 📋 Sequence 3: Basu Reviews Incoming Applications (Application Management)

Basu logs back in to check who applied for his job posting.

### 3.1 Basu Views Apps for the Job
- **Action**: Basu wants to view everyone who applied to the "Spring Boot Developer" job listing.
- **Endpoint**: `GET /api/applications/job/{jobId}`
- **Headers**: `Authorization: Bearer <BASU_TOKEN>`
- **What this does**: Returns a list of all candidates who applied. Basu will see Aditya's application UUID (e.g., `applicationId`) in this list.

### 3.2 Basu Adds Internal Notes
- **Action**: Basu reads Aditya's resume and wants to leave a private note for his hiring team.
- **Endpoint**: `PATCH /api/applications/{applicationId}/notes`
- **Headers**: `Authorization: Bearer <BASU_TOKEN>`
- **Payload**:
  ```json
  {
    "notes": "Strong background in Docker, but needs to brush up on Kafka."
  }
  ```
- **Details**: This is entirely internal. Aditya cannot see these notes.

### 3.3 Basu Shortlists Aditya
- **Action**: Based on the strong resume, Basu decides to move Aditya to the next round.
- **Endpoint**: `PATCH /api/applications/{applicationId}/status?status=SHORTLISTED`
- **Headers**: `Authorization: Bearer <BASU_TOKEN>`
- **Details**: Notice the status is passed as a query parameter in the URL. Acceptable statuses include `SHORTLISTED`, `REJECTED`, etc.

---

## 📅 Sequence 4: The Interview Dance (Collaboration Flow)

Basu is impressed by Aditya's application and wants to schedule an interview.

### 4.1 Basu Schedules the Interview
- **Endpoint**: `POST /api/applications/{applicationId}/interviews`
- **Headers**: `Authorization: Bearer <BASU_TOKEN>`
- **Payload**:
  ```json
  {
    "slotStart": "2026-05-15T10:00:00",
    "slotEnd": "2026-05-15T11:00:00",
    "mode": "VIDEO",
    "locationOrLink": "https://zoom.us/j/window-tec-meeting"
  }
  ```
- **Behind the Scenes**: Aditya's application status **automatically** changes from `SHORTLISTED` to `INTERVIEW_SCHEDULED`. The interview itself is created with a `PENDING` status.

### 4.2 Aditya Accepts the Interview
- **Action**: Aditya sees the interview notification and wants to lock in the time slot.
- **Endpoint**: `PATCH /api/interviews/{interviewId}/status?status=ACCEPTED`
- **Headers**: `Authorization: Bearer <ADITYA_TOKEN>`
- **Edge Case Error**: If Aditya tries to `CANCEL` or mark the interview as `COMPLETED`, the API will fail with an error `Seekers can only ACCEPT or REJECT interviews`. Aditya only has permission to say yes or no to the proposed time slot!

---

## 🛡️ Sequence 5: Ankit's Oversight (Admin Flow)

**Ankit** is the Platform Administrator. He monitors the system, oversees data access, and ensures companies are legitimate.

### 5.1 Ankit Registers/Logs In
- Ankit can register using the `ADMIN` role (or use an already existing admin account created via the `/reset-db` endpoint, e.g., `admin@jobtracker.com`).
- Once logged in, Ankit acts like a superuser who can monitor access and oversee the platform globally.

### 5.2 Ankit Verifies Window Tec Solutions
- **Action**: Ankit reviews "Window Tec Solutions" to make sure it's a real company, then grants them verification.
- **Endpoint**: `PATCH /api/companies/{companyId}/verify`
- **Headers**: `Authorization: Bearer <ANKIT_ADMIN_TOKEN>`
- **Result**: The "verified" badge is now set to `true` for Basu's company, instilling trust in seekers like Aditya.

---

## 📊 Sequence 6: Checking Progress (Dashboard Flow)

### 6.1 Aditya Checks His Seeker Dashboard
- **Endpoint**: `GET /api/dashboard/seeker`
- **Headers**: `Authorization: Bearer <ADITYA_TOKEN>`
- **Aditya Sees**:
    - Total Applications: 1
    - Total Interviews: 1
    - Funnel Breakdown: `INTERVIEW_SCHEDULED: 1`

### 6.2 Basu Checks His Company Dashboard
- **Endpoint**: `GET /api/dashboard/company`
- **Headers**: `Authorization: Bearer <BASU_TOKEN>`
- **Basu Sees**:
    - Total Jobs Posted: 1
    - Total Applicants: 1
    - Activity Data for his "Spring Boot Developer" listing.

---

## 🚫 How to Break the System (Testing Your Edge Cases)

To truly verify the backend's strength, try these "Malicious" acts:

1.  **The Identity Thief**: Login as **Aditya** (Seeker) and try to delete a Job Posting.
    *   **Result**: `403 Forbidden`. Only Basu (The owner) can delete his jobs.
2.  **The Time Traveler**: Try to schedule an interview for yesterday.
    *   **Result**: `400 Bad Request`. The system blocks past dates.
3.  **The Double Applier**: Aditya tries to apply to the same job twice.
    *   **Result**: `400 Bad Request`. We built a "Unique Constraint" to prevent spam.
4.  **The Unqualified Recruiter**: Register a new user as `JOB_SEEKER` and try to call `POST /api/companies`.
    *   **Result**: `403 Forbidden`. You must be a `COMPANY` role to even *request* to create a company profile.
5.  **The Ghost Interview**: Try to update an interview ID that doesn't exist.
    *   **Result**: `404 Not Found`.

---

## 📝 Appendix: Endpoint Summary Table

| Action | HTTP Method | Endpoint | Role Allowed |
| :--- | :--- | :--- | :--- |
| Reset DB | POST | `/api/dev/reset-db` | PUBLIC |
| Register | POST | `/api/auth/register` | PUBLIC |
| Login | POST | `/api/auth/login` | PUBLIC |
| Create Company | POST | `/api/companies` | COMPANY |
| Post Job | POST | `/api/companies/{id}/jobs` | COMPANY (Owner) |
| Apply Job | POST | `/api/jobs/{id}/apply` | JOB_SEEKER |
| View Applications | GET | `/api/applications/job/{jobId}` | COMPANY (Owner) |
| Update App Status | PATCH | `/api/applications/{id}/status` | COMPANY (Owner) |
| Add Internal Notes| PATCH | `/api/applications/{id}/notes` | COMPANY (Owner) |
| Schedule Interview | POST | `/api/applications/{id}/interviews` | COMPANY (Owner) |
| Update Int. Status | PATCH | `/api/interviews/{id}/status` | SEEKER / COMPANY |
| Verify Company | PATCH | `/api/companies/{id}/verify` | ADMIN |
| Seeker Stats | GET | `/api/dashboard/seeker` | JOB_SEEKER |
| Company Stats | GET | `/api/dashboard/company` | COMPANY |
