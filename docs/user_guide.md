# Smart Job Tracker - User Guide

This document tracks the current phase of our implementation and provides instructions on how to run and test the application at each step.

---

## Current Phase: Phase 1 (Initialization & Infrastructure)
The foundation of the project is set. We have established the package hierarchy, centralized error handling, and core application configuration.

### Prerequisites
- **Java:** Version 17 or higher.
- **Database:** PostgreSQL (must be running).
- **Python:** Python 3.x (optional, for running the Python test script).

---

## 1. How to Run the Application

### **Database Setup (Critical)**
Before running, you must configure your database credentials. 
1. Open `src/main/resources/application.properties`.
2. Update the following lines with your PostgreSQL username and password:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/smart_job_tracker
   spring.datasource.username=postgres
   spring.datasource.password=your_password
   ```

### **Run Command**
From the root directory of the project, execute:
```powershell
./mvnw spring-boot:run
```
**What this does:**
- Downloads all necessary dependencies.
- Compiles the Java source code.
- Starts the embedded Apache Tomcat server.
- Initializes the Spring Context.

### **Where is it running?**
Once the application starts successfully (you will see `Started JobTrackerApplication` in the logs), the project is accessible at:
- **Base URL:** `http://localhost:8080`

> [!NOTE]
> Since this is Phase 1 (Infrastructure), accessing the URL in a browser will currently show a "404 Not Found" or a Whitelabel Error Page. This is **expected** because we haven't implemented specific web pages yet—only the background skeleton.

---

## 2. Testing Your Progress

I have provided two dedicated test scripts to ensure Phase 1 is strictly followed and functional.

### **Method A: Java Native Test (Recommended)**
This uses the project's own build system (Maven) and JUnit 5.
```powershell
./mvnw test -Dtest=Phase1Test
```
**What it tests:**
- **Folder Integrity:** Checks if all 9 best-practice packages (controller, service, repository, etc.) exist.
- **Core Files:** Verifies the existence of the Exception Handler, main Application class, and required DTOs.
- **Compilation:** Indirectly proves that all Java code is syntactically correct and can be built.

---

### **Method B: Python Verification Script**
A lightweight script for a quick health check.
```bash
python scripts/test_phase1.py
```
**What it shows:**
- A step-by-step log of every folder it finds or misses.
- A final total compilation check by calling the Maven compiler.
- Detailed error messages if any file is misplaced or deleted.

---

## 3. Current API Endpoints (Infrastructure Only)
At this stage, the API is ready to handle errors but has no business routes yet.

| Endpoint | Method | Description |
| :--- | :--- | :--- |
| `http://localhost:8080/` | ANY | Returns a default error response managed by our `GlobalExceptionHandler`. |

---
*Next Phase: Phase 2 will introduce Security and Authentication (Login/Register).*
