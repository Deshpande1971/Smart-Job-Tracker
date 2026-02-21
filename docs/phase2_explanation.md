### 1. User Personas & Permissions
The system is designed with three distinct user types, each serving a specific role in the job market ecosystem:

#### **A. Job Seeker (JOB_SEEKER)**
*   **Persona**: Individual candidates looking for career opportunities.
*   **Workflow**: Profile creation -> Resume Upload -> Job Search -> Application Submission -> Status Tracking.
*   **Access**: Can view public jobs, apply to jobs, and manage their own applications. Forbidden from posting jobs or accessing company analytics.

#### **B. Company Recruiter (COMPANY)**
*   **Persona**: HR professionals or hiring managers representing an organization.
*   **Workflow**: Company Verification -> Job Posting -> Application Review -> Interview Scheduling -> Hiring decisions.
*   **Access**: Can create/edit job postings, view applications received for their jobs, and update application statuses.

#### **C. Administrator (ADMIN)**
*   **Persona**: System operators responsible for platform integrity.
*   **Workflow**: Company vetting -> Handling disputes -> Monitoring system health -> Managing global categories.
*   **Access**: Full access to the system. Can delete fraudulent posts, verify companies, and manage all users.

---

### 2. JWT (JSON Web Tokens)
Instead of the server remembering who is logged in (sessions), we give the user a "passport" (the token).


### 2. JWT (JSON Web Tokens)
Instead of the server remembering who is logged in (sessions), we give the user a "passport" (the token).
- **JwtUtils**: Logic to sign the token with a secret key and check if it's expired.
- **JwtAuthenticationFilter**: A security "gatekeeper" that checks every incoming request. If it sees a valid token, it tells Spring Security who the user is.

### 3. Password Security
- We use **BCryptPasswordEncoder**. 
- **Important**: Passwords are never stored as plain text. They are hashed before saving to the database. Even if the database is leaked, the actual passwords remain secure.

### 4. Stateless Architecture
The application is now **Stateless**. This means the server doesn't store any user state. This makes the app much faster and easier to scale to thousands of users.

---

## What changed from Phase 1?
- **Dependency**: Added `jjwt` library.
- **Security Logic**: Replaced the default prompted login with our manual API logic.
- **Access Control**: Swagger and Auth endpoints are open to everyone, but everything else now requires a valid token.
