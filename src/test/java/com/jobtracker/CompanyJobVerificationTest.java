package com.jobtracker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobtracker.dto.AuthResponseDTO;
import com.jobtracker.dto.CompanyRequestDTO;
import com.jobtracker.dto.JobRequestDTO;
import com.jobtracker.dto.RegistrationRequestDTO;
import com.jobtracker.entity.EmploymentType;
import com.jobtracker.entity.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CompanyJobVerificationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;
    private String companyId;

    @BeforeEach
    void setUp() throws Exception {
        // 1. Register a Recruiter
        String email = "recruiter_" + UUID.randomUUID() + "@test.com";
        RegistrationRequestDTO regRequest = RegistrationRequestDTO.builder()
                .name("Recruiter One")
                .email(email)
                .password("password123")
                .role(UserRole.COMPANY)
                .build();

        MvcResult regResult = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(regRequest)))
                .andExpect(status().isOk())
                .andReturn();

        AuthResponseDTO authResponse = objectMapper.readValue(
                regResult.getResponse().getContentAsString(),
                AuthResponseDTO.class);
        token = "Bearer " + authResponse.getToken();

        // 2. Register a Company
        CompanyRequestDTO companyRequest = CompanyRequestDTO.builder()
                .name("Tech Corp " + UUID.randomUUID())
                .description("A leading technology company with innovation at its core.")
                .industry("Software")
                .contactEmail("contact_" + UUID.randomUUID() + "@techcorp.com")
                .build();

        MvcResult compResult = mockMvc.perform(post("/api/companies")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(companyRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String content = compResult.getResponse().getContentAsString();
        companyId = objectMapper.readTree(content).get("id").asText();
    }

    @Test
    void testCreateJobAndSearch() throws Exception {
        // 1. Post a Job
        JobRequestDTO jobRequest = JobRequestDTO.builder()
                .title("Senior Java Developer")
                .description("We are looking for a senior Java expert.")
                .location("Remote")
                .employmentType(EmploymentType.FULL_TIME)
                .skills(List.of("Java", "Spring Boot", "Docker"))
                .salaryMin(80000.0)
                .salaryMax(120000.0)
                .isPublished(true)
                .build();

        mockMvc.perform(post("/api/companies/" + companyId + "/jobs")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(jobRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Senior Java Developer"))
                .andExpect(jsonPath("$.companyName").exists());

        // 2. Fetch public job list (No Auth required)
        mockMvc.perform(get("/api/jobs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[?(@.title == 'Senior Java Developer')]").exists());
    }

    @Test
    void testUnauthorizedJobPosting() throws Exception {
        // Try to post a job without token
        JobRequestDTO jobRequest = JobRequestDTO.builder()
                .title("Hacker Job")
                .description("This should fail.")
                .build();

        mockMvc.perform(post("/api/companies/" + companyId + "/jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(jobRequest)))
                .andExpect(status().isForbidden());
    }
}
