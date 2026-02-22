# Phase 5: Final Setup & Running Guide

This guide explains how to get the fully complete Smart Job Tracker running on your local machine.

## 🚀 Step 1: Automated Verification

To ensure all new Phase 5 features work perfectly, run this command in your terminal:

```powershell
./mvnw test -Dtest=InterviewVerificationTest,DashboardVerificationTest,Phase5DeepVerificationTest
```
*Tip: If all tests show "BUILD SUCCESS", your backend is 100% ready for action!*

---

## 🛠️ Step 2: Running the Project (Standard Way)

If you just want to run the app normally without Docker:
1. Ensure your PostgreSQL database is running.
2. Update `src/main/resources/application.properties` with your DB credentials.
3. Run:
   ```powershell
   ./mvnw spring-boot:run
   ```

---

## 🐳 Step 3: Running with Docker (The Simple Way)

Docker allows you to start the **Backend + Database** with zero manual configuration.

1. **Start Everything**:
   ```bash
   docker-compose up --build
   ```
2. **That's it!** The app will be alive at `http://localhost:8080`.

---

## 📖 Step 4: Exploring the API (Swagger)

We integrated **Swagger UI**. This is a website created automatically by our code that lets you see and click on all our API endpoints.

- **URL**: `http://localhost:8080/swagger-ui.html`
- **Use it to**:
    - See what data each endpoint requires.
    - Test the endpoints directly from your browser.
    - View the "Models" (objects) we use in our project.

---

## 🧪 Manual Testing Flow (For Beginners)

1. **Register** a Recruiter and a Seeker.
2. **Post a Job** as the Recruiter.
3. **Apply** to that job as the Seeker.
4. **Schedule an Interview** as the Recruiter using the Seeker's application ID.
5. **Check the Seeker Dashboard** to see the "Interview Count" increase!
