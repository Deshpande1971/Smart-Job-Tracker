# Smart Job Tracker: Frontend Blueprint & Architecture Guide

This document serves as the absolute source of truth for building the React.js frontend for the **Smart Job Tracker**. It contains exact API contracts, validation rules, state management advice, and architectural best practices.

## 🏗️ 1. Architecture: Monorepo vs. Separate Repo?

**Recommendation: Separate Repository (Standalone Frontend)**
*   **Why?** In modern web development, keeping your React code completely decoupled from your Spring Boot backend is the industry standard.

### 🆚 The Debate: Same Repo vs. Separate Repo

**Option A: The Same Repo (Monolithic / Monorepo)**
*   **How it works:** You would put your React files inside the Spring Boot `src/main/resources/static` folder, or use Maven to build both at the same time.
*   **Pros:** Easier to deploy as a single container (one Docker image).
*   **Cons:** Extremely painful local development. Your Java IDE (IntelliJ/Eclipse) gets confused by JavaScript packages. Your Git history becomes a messy mix of Backend bugs and Frontend CSS tweaks. 

**Option B: Separate Repositories (⭐ THE WINNER)**
*   **How it works:** You create a completely brand new, empty Git repository just for the React code.
*   **Pros (Why it's Best Practice):**
    1.  **Clear Separation of Concerns:** Your frontend engineers (or you!) only need to load the React code in VS Code, while the backend stays in IntelliJ.
    2.  **Independent Scaling:** If your UI gets popular, you can host your React app for free on Vercel/Netlify, while hosting your heavy Java backend on AWS/Render.
    3.  **Clean Version Control:** When you look at Git commits in the frontend repo, you only see UI changes.

### 🔌 How to connect the Two Separate Repos:
Since they are in separate folders, your React app (running on `localhost:3000` or `5173`) needs to know how to "talk" to the Java app (`localhost:8080`).

1.  **Do NOT Hardcode the IP:** Never write `axios.post('http://localhost:8080/api/auth/login')` directly in your code. If you do this, your app will break the second you deploy it to the internet because the backend won't be on "localhost" anymore!
2.  **Use Environment Variables:**
    *   In the root of your new React project, create a file called `.env`
    *   Inside it, write: `VITE_API_BASE_URL=http://localhost:8080` (If using Vite) or `REACT_APP_API_BASE_URL=http://localhost:8080` (If using Create React App).
    *   In your React code, you reference the server dynamically: `axios.post(\`${import.meta.env.VITE_API_BASE_URL}/api/auth/login\`)`

---

## 🔐 2. Authentication & State Management

**Handling the JWT Token:**
1.  **Storage:** When the user logs in, store the `JWT Token` in `localStorage` or `sessionStorage`. Do NOT store passwords.
    *   *Example:* `localStorage.setItem('token', response.data.token);`
2.  **API Requests:** Create an Axios Interceptor (or a custom `fetch` wrapper) that automatically attaches `Authorization: Bearer <TOKEN>` to every request if the token exists.
3.  **Roles:** The UI needs to know if the user is a `JOB_SEEKER`, `COMPANY`, or `ADMIN`. You can decode the JWT token on the frontend (using a library like `jwt-decode`) to extract the `role` and conditionally render navigation menus.

**ID Handling (`companyId`, `jobId`, `applicationId`):**
*   **Never persist transient IDs in `localStorage`** (except maybe the user's primary `companyId` if they are a recruiter).
*   **Use URL Routing:** If a recruiter is viewing applicants for a specific job, the URL should be `/jobs/:jobId/applications`. React Router will extract `jobId` and pass it to the API.

---

## 📡 3. API Endpoints & Strict Validation Rules

Below are the exact payloads and validations enforced by the backend (`jakarta.validation`). The frontend **must** validate these before sending data to prevent `400 Bad Request` errors.

### 👥 A. Authentication

**1. Register Account**
*   **Endpoint:** `POST /api/auth/register`
*   **Payload:**
    ```javascript
    {
      "name": "string",     // REQUIRED: Cannot be blank.
      "email": "string",    // REQUIRED: Must be a valid email format.
      "password": "string", // REQUIRED: Must be at least 6 characters long.
      "role": "string"      // REQUIRED: Must be "JOB_SEEKER", "COMPANY", or "ADMIN"
    }
    ```

**2. Login**
*   **Endpoint:** `POST /api/auth/login`
*   **Payload:**
    ```javascript
    {
      "email": "string",
      "password": "string"
    }
    ```

---

### 🏢 B. Recruiter / Company Workflows

**1. Create Company Profile**
*   **Endpoint:** `POST /api/companies`
*   **Auth:** Requires `Bearer Token` (Role: `COMPANY`)
*   **Payload & Frontend Validations:**
    ```javascript
    {
      "name": "string",          // REQUIRED: Cannot be blank
      "description": "string",   // REQUIRED: Minimum 20 characters! (IMPORTANT)
      "industry": "string",      // REQUIRED: Cannot be blank
      "contactEmail": "string",  // REQUIRED: Valid email format
      "companySize": "string",   // Optional
      "headquarters": "string",  // Optional
      "website": "string",       // Optional
      "logoUrl": "string"        // Optional
    }
    ```

**2. Post a Job**
*   **Endpoint:** `POST /api/companies/{companyId}/jobs`
*   **Payload:**
    ```javascript
    {
      "title": "string",               // REQUIRED: Cannot be blank
      "description": "string",         // REQUIRED: Cannot be blank
      "location": "string",            // Optional
      "employmentType": "string",      // Optional (Enum: FULL_TIME, PART_TIME, CONTRACT, FREELANCE, INTERNSHIP)
      "applicationDeadline": "string", // Optional: Must be a FUTURE Date (e.g., "2026-12-31T23:59:59")
      "isPublished": true              // Default: true
    }
    ```

---

### 💼 C. Job Seeker Workflows

**1. Apply for a Job**
*   **Endpoint:** `POST /api/jobs/{jobId}/apply`
*   **Auth:** Requires `Bearer Token` (Role: `JOB_SEEKER`)
*   **Payload:**
    ```javascript
    {
      "resumeUrl": "string",   // REQUIRED: Cannot be blank (URL format)
      "coverLetter": "string"  // Optional text
    }
    ```
*   **Watch out:** If you send the same `jobId` twice for the same user, you will get a `400 Bad Request` (Unique constraint violation). Handle this gracefully in the UI.

---

### 📅 D. Interview & Collaboration

**1. Schedule an Interview (Recruiter only)**
*   **Endpoint:** `POST /api/applications/{applicationId}/interviews`
*   **Payload:**
    ```javascript
    {
      "slotStart": "datetime",      // REQUIRED: Must be a FUTURE date/time (e.g., "2026-05-15T10:00:00")
      "slotEnd": "datetime",        // REQUIRED: Must be a FUTURE date/time
      "mode": "string",             // REQUIRED: Enum ("VIDEO", "IN_PERSON", "TELEPHONY")
      "locationOrLink": "string"    // REQUIRED: Cannot be blank
    }
    ```

**2. Accept/Reject Interview (Seeker only)**
*   **Endpoint:** `PATCH /api/interviews/{interviewId}/status?status=ACCEPTED`
*   **Valid Queries:** `status=ACCEPTED` or `status=REJECTED`
*   **Watch out:** Seekers cannot send `status=COMPLETED`. The UI should only show Accept/Reject buttons for seekers.

---

## 🚀 4. Recommended React Folder Structure

To keep your codebase clean, follow this modular structure:
```text
src/
├── api/             # Axios instances and API wrapper functions (e.g., authApi.js, jobApi.js)
├── components/      # Reusable UI components (Buttons, Modals, Navbar)
├── pages/           # Full page views (Dashboard, JobBoard, Login, Register)
├── contexts/        # React Context API for global state (AuthContext.jsx)
├── hooks/           # Custom React hooks (useAuth, useFetch)
├── utils/           # Helper functions (date formatting, token decoder)
└── App.jsx          # Route declarations
```

## ⚠️ 5. UI/UX Error Handling Checklist
*   **Global Loader:** Show a spinner while the API responds.
*   **Toast Notifications:** When a `200 OK` is received (e.g., Job Applied), show a green toast message.
*   **Form Errors:** If the API returns a `400 Bad Request` (e.g., Password < 6 chars, Description < 20 chars), parse the JSON error payload and display the message directly under the offending input field in red text.
*   **Token Expiry:** If an API call returns `401 Unauthorized`, your Axios interceptor should automatically clear `localStorage` and redirect the user back to the `/login` page.
