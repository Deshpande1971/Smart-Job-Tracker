package com.jobtracker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobtracker.dto.*;
import com.jobtracker.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class InterviewVerificationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String seekerToken;
    private String recruiterToken;
    private String applicationId;

    @BeforeEach
    void setUp() throws Exception {
        // 1. Recruiter
        String recEmail = "rec_" + UUID.randomUUID() + "@test.com";
        recruiterToken = "Bearer " + registerAndGetToken("Recruiter", recEmail, UserRole.COMPANY);

        // 2. Company
        CompanyRequestDTO compReq = CompanyRequestDTO.builder().name("Interview Corp")
                .description("Desc min 20 characters required").industry("Tech").contactEmail("c@c.com").build();
        String compResp = mockMvc
                .perform(post("/api/companies").header("Authorization", recruiterToken)
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(compReq)))
                .andReturn().getResponse().getContentAsString();
        String companyId = objectMapper.readTree(compResp).get("id").asText();

        // 3. Job
        JobRequestDTO jobReq = JobRequestDTO.builder().title("Dev").description("Desc").location("Remote")
                .employmentType(EmploymentType.FULL_TIME).isPublished(true).build();
        String jobResp = mockMvc
                .perform(post("/api/companies/" + companyId + "/jobs").header("Authorization", recruiterToken)
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(jobReq)))
                .andReturn().getResponse().getContentAsString();
        String jobId = objectMapper.readTree(jobResp).get("id").asText();

        // 4. Seeker
        String seekEmail = "seek_" + UUID.randomUUID() + "@test.com";
        seekerToken = "Bearer " + registerAndGetToken("Seeker", seekEmail, UserRole.JOB_SEEKER);

        // 5. Apply
        ApplicationRequestDTO appReq = ApplicationRequestDTO.builder().resumeUrl("http://res.pdf").build();
        String appResp = mockMvc
                .perform(post("/api/jobs/" + jobId + "/apply").header("Authorization", seekerToken)
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(appReq)))
                .andReturn().getResponse().getContentAsString();
        applicationId = objectMapper.readTree(appResp).get("id").asText();
    }

    private String registerAndGetToken(String name, String email, UserRole role) throws Exception {
        RegistrationRequestDTO reg = RegistrationRequestDTO.builder().name(name).email(email).password("pass123")
                .role(role).build();
        String resp = mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reg))).andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(resp, AuthResponseDTO.class).getToken();
    }

    @Test
    void testInterviewFlow() throws Exception {
        // 1. Schedule Interview
        InterviewRequestDTO intReq = InterviewRequestDTO.builder()
                .slotStart(LocalDateTime.now().plusDays(1))
                .slotEnd(LocalDateTime.now().plusDays(1).plusHours(1))
                .mode(InterviewMode.VIDEO)
                .locationOrLink("http://zoom.us/j/123")
                .build();

        String intResp = mockMvc.perform(post("/api/applications/" + applicationId + "/interviews")
                .header("Authorization", recruiterToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(intReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andReturn().getResponse().getContentAsString();

        String interviewId = objectMapper.readTree(intResp).get("id").asText();

        // Verify application status changed
        mockMvc.perform(get("/api/applications/my").header("Authorization", seekerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("INTERVIEW_SCHEDULED"));

        // 2. Seeker accepts interview
        mockMvc.perform(patch("/api/interviews/" + interviewId + "/status")
                .header("Authorization", seekerToken)
                .param("status", "ACCEPTED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"));
    }
}
