# Phase 3 Bug Fixes & Troubleshooting Report

This document summarizes the technical challenges encountered during the implementation of Company and Job Management, the exploration process, and the final architectural solutions.

---

## 1. Issue: Security Filter Returning 500 instead of 403
### **Problem Discovery**
While running `CompanyJobVerificationTest.testUnauthorizedJobPosting`, the test failed with:
`java.lang.AssertionError: Status expected:<403> but was:<500>`

### **Exploration**
- Checked the logs: Spring Security was correctly throwing an `AccessDeniedException` because the request lacked the `COMPANY` role.
- However, our `GlobalExceptionHandler` had a catch-all `@ExceptionHandler(Exception.class)` which was intercepting the security exception and wrapping it as a generic "Internal Server Error".

### **Final Fix**
Explicitly handled the security exception in `GlobalExceptionHandler.java`:
```java
@ExceptionHandler(AccessDeniedException.class)
public ResponseEntity<ErrorResponseDTO> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
    return buildErrorResponse("Access Denied: You do not have permission...", HttpStatus.FORBIDDEN, request);
}
```
**Result**: The API now correctly returns a 403 Forbidden with a clean JSON body.

---

## 2. Issue: Non-Descriptive Errors for Business Rule Violations
### **Problem Discovery**
Duplicate company registrations were returning a 500 error with the message "Company name already exists".

### **Exploration**
- The `CompanyService` was throwing a `RuntimeException`. 
- Per REST best practices, business rule violations (like duplicates) should return a **400 Bad Request**, not a 500 server error.

### **Final Fix**
1. Created a custom `AlreadyExistsException`.
2. Updated `CompanyService` and `AuthService` to throw this specific exception.
3. Updated `GlobalExceptionHandler` to map this exception to `HttpStatus.BAD_REQUEST`.

---

## 3. Issue: Test Suite Pollution (Duplicate Emails)
### **Problem Discovery**
`CompanyJobEdgeCaseTest` would pass on the first run but fail on subsequent runs with "Email already exists".

### **Exploration**
- The test was using hardcoded strings like `recruiter_a@test.com`.
- Even though the database resets in some environments, the local development database was retaining users from previous test runs.

### **Final Fix**
1. **Dynamic Data**: Appended `UUID.randomUUID()` to all emails and company names within the test logic.
2. **Isolation**: Added the `@Transactional` annotation to the test classes. This ensures that every database change made during a test is rolled back immediately after the test finishes.

---

## 4. Issue: Cross-Company Posting Security Flaw
### **Problem Discovery**
During manual code review, it was identified that a Recruiter with a valid token could potentially post a job for *any* company if they knew the `companyId`.

### **Exploration**
- Standard Role-based access (`hasRole('COMPANY')`) only checks *if* you are a recruiter, not *which* company you belong to.

### **Final Fix**
Implemented a strict ownership check in `JobService.java`:
```java
if (currentUser.getCompany() == null || !currentUser.getCompany().getId().equals(companyId)) {
    throw new AlreadyExistsException("Unauthorized: You do not belong to this company");
}
```
**Result**: Secured the recruitment pipeline so recruiters can only manage their own organization's data.

---

## ✅ Summary of What Worked
- **Custom Exception Hierarchy**: Moving away from `RuntimeException` to specific business exceptions allows for fine-grained HTTP status control.
- **Transactional Tests**: Crucial for maintaining a "Clean State" in the development database.
- **Method-Level Security + Service Validation**: Combining `@PreAuthorize` with manual ID checks in the service layer provided the highest level of security.
