com.jobtracker
│
├── config
│     ├── SecurityConfig.java
│     ├── SwaggerConfig.java
│
├── controller
│     ├── AuthController.java
│     ├── JobController.java
│     ├── CompanyController.java
│
├── service
│     ├── AuthService.java
│     ├── JobService.java
│     ├── CompanyService.java
│     ├── impl
│           ├── AuthServiceImpl.java
│           ├── JobServiceImpl.java
│
├── repository
│     ├── UserRepository.java
│     ├── JobRepository.java
│
├── entity
│     ├── User.java
│     ├── Job.java
│     ├── Company.java
│
├── dto
│     ├── LoginRequestDTO.java
│     ├── JobResponseDTO.java
│
├── security
│     ├── JwtFilter.java
│     ├── JwtUtil.java
│     ├── CustomUserDetailsService.java
│
├── exception
│     ├── GlobalExceptionHandler.java
│     ├── ResourceNotFoundException.java
│
├── util
│     ├── DateUtil.java
│
└── JobTrackerApplication.java