# Smart Job Tracker: Backend Architecture & Development Approach

This document serves as a comprehensive technical overview of the "Smart Job Tracker" backend. It explains the architectural patterns, the reasoning behind our technology choices, and the journey from a concept to a production-ready API.

---

## 🏗️ 1. System Architecture
We adopted a **Tiered Monolithic Architecture**. This is the gold standard for MVPs (Minimum Viable Products) because it keeps the codebase together while maintaining a strict separation of concerns.

### The 4-Layer Pattern:
1.  **Controller Layer (API)**: Handles HTTP requests, validates incoming JSON (using JSR-303), and returns clean DTOs.
2.  **Service Layer (Business Logic)**: The "brain" of the app. It handles security checks, calculations, and orchestrates data flow between repositories.
3.  **Repository Layer (Data)**: Leverages **Spring Data JPA**. We chose this over writing manual SQL because it provides "out-of-the-box" methods like `existsByEmail` or `findByStatus`, which accelerates development and reduces bugs.
4.  **Database Layer (Storage)**: Uses **PostgreSQL**.

### **Why this architecture?**
-   **Clarity**: A beginner can look at the folders and immediately know where the logic lives.
-   **Maintainability**: If we want to change how "Passwords" are saved, we only touch the `AuthService`, not the whole app.
-   **Alternatives**: We could have used **Microservices**, but for a Job Tracker, the network overhead and complexity of managing multiple databases would have slowed us down significantly.

---

## 🔒 2. Security & Identity Management
The core of the app is **Stateless Security** using **JWT (JSON Web Tokens)**.

### **How it works:**
1.  User logs in.
2.  Server verifies credentials and generates a signed token (the JWT).
3.  The Server **does not store** this token; the User carries it.
4.  For every subsequent request, the user sends the token in the header.

### **Why JWT over Sessions?**
-   **Scalability**: If we have 10,000 users, the server memory stays low because it doesn't need to save session data in RAM.
-   **Mobile Ready**: JWTs are much easier to handle for future mobile apps (iOS/Android) than browser cookies.
-   **Alternatives**: **Session/Cookies** are fine, but they require "sticky sessions" or a shared Redis cache if we ever want to scale the backend.

---

## 📊 3. Data Modeling & Relationships
We designed the database to reflect the real-world hiring lifecycle:
-   **Users & Roles**: Using a single `users` table with a `role` enum. This simplifies login logic.
-   **Jobs & Companies**: A **1-to-Many** relationship. A company posts many jobs.
-   **Applications**: The "Bridge" between a Seeker and a Job. It tracks the status (`APPLIED`, `OFFERED`, `REJECTED`).
-   **Interviews**: Linked to the Application. This creates a clear timeline of the candidate's journey.

---

## 🚀 4. Development Approach: The "Phased" Journey

### **Phase 1-2: The Foundation**
We started with **Authentication** and **Security**. 
-   **Philosophy**: You cannot build a community (Job Board) without a secure gate (Login). We used `@PreAuthorize` tags early on to bake security directly into our API design.

### **Phase 3-4: The Core Features**
We built the ability to **Post Jobs** and **Track Applications**.
-   **Approach**: We used DTOs (Data Transfer Objects). 
-   **Why?**: To avoid "Overposting". If we sent the raw `User` object to the frontend, we might accidentally leak his hashed password. DTOs act as a filter, sending only safe, necessary data.

### **Phase 5: Intelligence & Deployment**
We added **Dashboards** and **Docker**.
-   **The Dashboards**: We chose **Dynamic Aggregation** (calculating stats on the fly) rather than saving them in a table.
-   **Why?**: For an MVP, data is small. Calculating "Total Applications" live ensures the numbers are always 100% accurate.
-   **Alternatives**: **Scheduled Tasks** (updating a "stats" table every hour). This is better for millions of records but adds too much complexity for this stage.

---

## 🐳 5. Why Docker?
We chose Docker because it solves the "It works on my machine" problem.
-   A beginner might have PostgreSQL 11, while another has PostgreSQL 16. This could cause crashes.
-   Docker packages the exact version of the database and the app into a single environment.
-   **Alternative**: Manual setup instructions. This is prone to human error and takes hours to debug.

---

## 💡 6. Lessons Learned & Future Scope
-   **Soft Deletes**: Currently, we delete records permanently. In a real-world app, we should use a `deleted` flag to keep historical data.
-   **Email Integration**: The next logical step is to integrate a Mail Server (like Mailgun) so seekers get an email when an interview is scheduled.
