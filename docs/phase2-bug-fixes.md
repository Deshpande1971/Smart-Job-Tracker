# Phase 2 Development Report: Challenges & Solutions

This document summarizes the technical hurdles encountered during the implementation of the JWT Security layer (Phase 2) and how they were systematically resolved. This is intended for presentation as a record of professional technical problem-solving.

## 1. Spring Boot Version Conflict (v4.0.3 vs v3.4.2)
**Issue**: The initial project configuration attempted to use Spring Boot version `4.0.3` (Managed by the workspace's default generator). This led to several "No plugin found" errors in Maven and incompatible class structures for core security components.
**Exploration**: Maven logs showed that many standard plugins (like `spring-boot-maven-plugin`) were not yet fully optimized for the unreleased v4 structure in this environment.
**Fix**: Downgraded the `spring-boot-starter-parent` to the production-stable version `3.4.2`. This restored compatibility with the repository ecosystem and IDE linting.

## 2. Bean Naming Conflict in SecurityConfig
**Issue**: A compilation error occurred because a custom `@Bean` was named `authenticationProvider()`.
**Exploration**: Spring Security's `HttpSecurity` configuration already has an internal method/reference named `authenticationProvider`. Defining a bean with the exact same name caused a signature mismatch/shadowing issue.
**Fix**: Renamed the local bean to `customAuthenticationProvider()` and updated the `securityFilterChain` to call this specific method.

## 3. Dependency Incompatibility (DaoAuthenticationProvider)
**Issue**: Compilation error: `constructor DaoAuthenticationProvider... cannot be applied to given types`.
**Exploration**: During the transition between Spring Security versions, the constructor-based injection for `UserDetailsService` changed. Attempting to pass the service directly into the constructor failed in the targeted version.
**Fix**: Switched to the more flexible setter-based injection:
```java
DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
authProvider.setUserDetailsService(userDetailsService);
authProvider.setPasswordEncoder(passwordEncoder());
```

## 4. Test Infrastructure Failures
**Issue**: JUnit tests failed to compile with the error: `The import org.springframework.boot.test... cannot be resolved`.
**Exploration**: The initial `pom.xml` was missing the foundational `spring-boot-starter-test` dependency, and several redundant "partial" test starters (like `webmvc-test`) were causing classpath confusion.
**Fix**: 
1. Consolidated dependencies by adding `spring-boot-starter-test`.
2. Added `spring-security-test` to enable `MockMvc` security context mocking.
3. Performed a `mvn clean` to purge corrupted `.class` files from previous failed compilation attempts.

## 5. MockMvc Access Exceptions
**Issue**: Some edge-case tests (invalid login/duplicate registration) were returning generic 500 errors instead of specific messages.
**Exploration**: Spring Security hides authentication details by default for security. 
**Fix**: Implemented `@Transactional` on the test class to ensure database state is rolled back between tests, and verified that `MockMvc` correctly captures the "Forbidden" status for unauthorized attempts.

---

### **Key Takeaway**
The most critical lesson from this phase was the importance of **Version Management**. By aligning the project with stable release cycles (v3.4.2) and resolving naming conflicts early, we established a robust foundation for the role-based access control (RBAC) that follows in Phase 3.
