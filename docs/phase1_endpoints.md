# Smart Job Tracker - Phase 1 Endpoint Documentation

This document describes the endpoints available at the end of **Phase 1 (Infrastructure & Setup)**.

## 1. Core Endpoints

| Endpoint | Method | Access | Description | What you will see |
| :--- | :--- | :--- | :--- | :--- |
| `/` | ANY | Private | Root application URL. | Redirects to a default Spring Security Login page. |
| `/swagger-ui.html` | GET | **Public** | Interactive API Documentation. | The Swagger UI dashboard for testing endpoints. |
| `/v3/api-docs` | GET | **Public** | OpenAPI Specification. | Raw JSON documentation for the API structure. |

---

## 2. Global Error Handling
In Phase 1, we implemented a centralized error handling system. If you try to access any non-existent page or trigger an error, you will see a structured JSON response instead of a genetic browser error.

### Example Response for Missing Pages
If you visit `http://localhost:8080/random-page`, you will receive:
```json
{
  "timestamp": "2026-02-21T17:40:00",
  "status": 404,
  "error": "Not Found",
  "message": "No static resource random-page.",
  "path": "/random-page"
}
```

---

## 3. What the User Can See (Visual Experience)

### **A. Default Login Screen**
Until we build our own Login page in Phase 2, Spring Security provides a basic "standard" login form. This confirms that our **Security Infrastructure** is active.
*   **Username:** `user`
*   **Password:** Found in the terminal logs when starting the app.

### **B. Swagger Dashboard**
By visiting `/swagger-ui.html`, you will see the interactive documentation. 
*   **Current Content:** You will only see "basic-error-controller" endpoints.
*   **Future Growth:** As we add Features (Jobs, Users, etc.), they will automatically appear here for testing.

### **C. Database Connection Status**
While not a web endpoint, you can run `.\test_files\run_db_test.ps1` in your terminal to see a visual confirmation of your PostgreSQL connection.

---
*Next Phase: We will introduce `/api/auth/register` and `/api/auth/login` with custom UI screens.*
