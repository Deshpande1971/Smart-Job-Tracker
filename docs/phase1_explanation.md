# Phase 1 Implementation Details: Initialization & Infrastructure

This document explains the **What**, **How**, and **Why** of the changes implemented in Phase 1 of the Smart Job Tracker project.

---

## 1. Project Skeleton & Package Structure
### **What:**
I created a comprehensive directory structure under `src/main/java/com/jobtracker`.
- `config`: Configuration classes (Security, Swagger, etc.).
- `controller`: REST API endpoints.
- `service`: Business logic interfaces and implementations (in `impl`).
- `repository`: Data access layer (Spring Data JPA).
- `entity`: Database models.
- `dto`: Data Transfer Objects for API requests/responses.
- `security`: JWT and Auth logic.
- `exception`: Custom exception handling.
- `util`: Helper classes.

### **Why:**
Adhering to a standardized package structure ensures **Separation of Concerns**. It makes the codebase maintainable, scalable, and easy to navigate for any developer. This specific structure follows the "Best Practices" document you provided.

---

## 2. Centralized Exception Handling
### **What:**
- **`GlobalExceptionHandler.java`**: A class annotated with `@RestControllerAdvice` that intercepts exceptions across the entire application.
- **`ErrorResponseDTO.java`**: A standard blueprint for all error messages returned by the API.
- **`ResourceNotFoundException.java`**: A custom runtime exception for when requested data (like a Job or User) doesn't exist.

### **How:**
When a `ResourceNotFoundException` is thrown anywhere in the service layer, the `GlobalExceptionHandler` catches it and returns a clean, formatted JSON response (with timestamp, status code, and message) instead of a messy stack trace.

### **Why:**
**User Experience & Security.** Consistent error messages help frontend developers handle failures easily. Also, hiding internal stack traces prevents leaking sensitive system information to potential attackers.

---

## 3. Database Configuration Skeleton
### **What:**
Updated `src/main/resources/application.properties` with PostgreSQL configuration placeholders and JPA/Hibernate settings.

### **How:**
I added properties for:
- `spring.datasource.*`: Driver, URL, and credentials.
- `hibernate.ddl-auto=update`: Automatically creates or updates database tables based on Java entities.
- `hibernate.dialect`: Tells Hibernate to use PostgreSQL-specific SQL syntax.

### **Why:**
Spring Boot needs these properties to establish a connection with your database. Moving from hardcoded values to properties allows us to easily change databases between development, testing, and production environments.

---

## 4. Main Application Class
### **What:**
Created `JobTrackerApplication.java`.

### **How:**
Annotated with `@SpringBootApplication`, which is a "super-annotation" that enables:
1. **Auto-Configuration**: Guesses and configures beans based on your classpath.
2. **Component Scanning**: Finds your controllers, services, and repositories.

### **Why:**
This is the entry point of your entire application. Without it, the Spring context cannot start.

---

## 5. Verification Scripts (Python & Java)
### **What:**
Created `scripts/test_phase1.py` and `src/test/java/com/jobtracker/Phase1Test.java`.

### **Why:**
To ensure that Phase 1 is "Rock Solid" before building authentication or business logic on top of it. Automated tests catch missing files or compilation errors instantly.
