package com.jobtracker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobtracker.dto.*;
import com.jobtracker.entity.ApplicationStatus;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ApplicationVerificationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String seekerToken;
    private String recruiterToken;
    private String companyId;
    private String jobId;

    @BeforeEach
    void setUp() throws Exception {
        // 1. Setup Recruiter and Company
        String recEmail = "recruiter_" + UUID.randomUUID() + "@test.com";
        recruiterToken = "Bearer " + registerAndGetToken("Recruiter", recEmail, UserRole.COMPANY);

        CompanyRequestDTO companyRequest = CompanyRequestDTO.builder()
                .name("Test Corp " + UUID.randomUUID())
                .description("A high tech testing company for applications.")
                .industry("Tech")
                .contactEmail("contact_" + UUID.randomUUID() + "@test.com")
                .build();

        String compResponse = mockMvc.perform(post("/api/companies")
                .header("Authorization", recruiterToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(companyRequest)))
                .andReturn().getResponse().getContentAsString();
        companyId = objectMapper.readTree(compResponse).get("id").asText();

        // 2. Post a Job
        JobRequestDTO jobRequest = JobRequestDTO.builder()
                .title("Software Engineer")
                .description("Build amazing things.")
                .location("Remote")
                .employmentType(EmploymentType.FULL_TIME)
                .isPublished(true)
                .build();

        String jobResponse = mockMvc.perform(post("/api/companies/" + companyId + "/jobs")
                .header("Authorization", recruiterToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(jobRequest)))
                .andReturn().getResponse().getContentAsString();
        jobId = objectMapper.readTree(jobResponse).get("id").asText();

        // 3. Setup Seeker
        String seekerEmail = "seeker_" + UUID.randomUUID() + "@test.com";
        seekerToken = "Bearer " + registerAndGetToken("Seeker", seekerEmail, UserRole.JOB_SEEKER);
    }

    private String registerAndGetToken(String name, String email, UserRole role) throws Exception {
        RegistrationRequestDTO reg = RegistrationRequestDTO.builder()
                .name(name)
                .email(email)
                .password("password123")
                .role(role)
                .build();

        String response = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reg)))
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readValue(response, AuthResponseDTO.class).getToken();
    }

    @Test
    void testApplicationFlow() throws Exception {
        // 1. Seeker applies to job
        ApplicationRequestDTO applyRequest = ApplicationRequestDTO.builder()
                .resumeUrl("http://s3.amazonaws.com/resumes/my-resume.pdf")
                .coverLetter("I am very interested in this position.")
                .build();

        String appResponse = mockMvc.perform(post("/api/jobs/" + jobId + "/apply")
                .header("Authorization", seekerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(applyRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPLIED"))
                .andExpect(jsonPath("$.jobTitle").value("Software Engineer"))
                .andReturn().getResponse().getContentAsString();

        String applicationId = objectMapper.readTree(appResponse).get("id").asText();

        // 2. Try to apply again (should fail)
        mockMvc.perform(post("/api/jobs/" + jobId + "/apply")
                .header("Authorization", seekerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(applyRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("You have already applied for this job"));

        // 3. Company updates status to SHORTLISTED
        StatusUpdateRequestDTO updateRequest = StatusUpdateRequestDTO.builder()
                .status(ApplicationStatus.SHORTLISTED)
                .notes("Great resume, let's interview.")
                .build();

        mockMvc.perform(patch("/api/applications/" + applicationId + "/status")
                .header("Authorization", recruiterToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SHORTLISTED"))
                .andExpect(jsonPath("$.recruiterNotes").value("Great resume, let's interview."));

        // 4. Seeker checks their applications
        mockMvc.perform(get("/api/applications/my")
                .header("Authorization", seekerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("SHORTLISTED"));
    }

    @Test
    void testUnauthorizedStatusUpdate() throws Exception {
        // 1. Seeker applies
        ApplicationRequestDTO applyRequest = ApplicationRequestDTO.builder()
                .resumeUrl("http://resume.pdf")
                .build();
        String appResponse = mockMvc.perform(post("/api/jobs/" + jobId + "/apply")
                .header("Authorization", seekerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(applyRequest)))
                .andReturn().getResponse().getContentAsString();
        String applicationId = objectMapper.readTree(appResponse).get("id").asText();

        // 2. Another recruiter (different company) tries to update status
        String otherRecEmail = "other_" + UUID.randomUUID() + "@test.com";
        String otherRecToken = "Bearer " + registerAndGetToken("Other Recruiter", otherRecEmail, UserRole.COMPANY);

        StatusUpdateRequestDTO updateRequest = StatusUpdateRequestDTO.builder()
                .status(ApplicationStatus.REJECTED)
                .build();

        mockMvc.perform(patch("/api/applications/" + applicationId + "/status")
                .header("Authorization", otherRecToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isInternalServerError()) // RuntimeException maps to 500
                .andExpect(jsonPath("$.message")
                        .value("Unauthorized: You do not have permission to manage this application"));
    }
}
