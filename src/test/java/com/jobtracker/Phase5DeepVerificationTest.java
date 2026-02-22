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
public class Phase5DeepVerificationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String seekerAToken, seekerBToken;
    private String recruiterAToken, recruiterBToken;
    private String companyAId, jobAId, applicationAId;

    @BeforeEach
    void setUp() throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        // Setup tokens
        recruiterAToken = "Bearer " + registerAndGetToken("RecA", "reca" + suffix + "@test.com", UserRole.COMPANY);
        recruiterBToken = "Bearer " + registerAndGetToken("RecB", "recb" + suffix + "@test.com", UserRole.COMPANY);
        seekerAToken = "Bearer " + registerAndGetToken("SeekA", "seeka" + suffix + "@test.com", UserRole.JOB_SEEKER);
        seekerBToken = "Bearer " + registerAndGetToken("SeekB", "seekb" + suffix + "@test.com", UserRole.JOB_SEEKER);

        // Setup Company for A
        CompanyRequestDTO compReq = CompanyRequestDTO.builder().name("Company A " + suffix)
                .description("Description for Company A with min 20 chars").industry("IT")
                .contactEmail("a" + suffix + "@a.com")
                .build();
        String compResp = mockMvc
                .perform(post("/api/companies").header("Authorization", recruiterAToken)
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(compReq)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        companyAId = objectMapper.readTree(compResp).get("id").asText();

        // Setup Job for A
        JobRequestDTO jobReq = JobRequestDTO.builder().title("Java Dev").description("Desc").isPublished(true).build();
        String jobResp = mockMvc
                .perform(post("/api/companies/" + companyAId + "/jobs").header("Authorization", recruiterAToken)
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(jobReq)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        jobAId = objectMapper.readTree(jobResp).get("id").asText();

        // Seeker A applies to Job A
        ApplicationRequestDTO appReq = ApplicationRequestDTO.builder().resumeUrl("http://resume.com/a").build();
        String appResp = mockMvc
                .perform(post("/api/jobs/" + jobAId + "/apply").header("Authorization", seekerAToken)
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(appReq)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        applicationAId = objectMapper.readTree(appResp).get("id").asText();
    }

    private String registerAndGetToken(String name, String email, UserRole role) throws Exception {
        RegistrationRequestDTO reg = RegistrationRequestDTO.builder().name(name).email(email).password("password123")
                .role(role).build();
        String resp = mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reg))).andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(resp, AuthResponseDTO.class).getToken();
    }

    /**
     * Test scheduling security: Only the company that owns the job can schedule
     * interviews.
     */
    @Test
    void testSecurity_RecruiterB_CannotScheduleFor_CompanyA_Application() throws Exception {
        InterviewRequestDTO intReq = InterviewRequestDTO.builder()
                .slotStart(LocalDateTime.now().plusDays(2))
                .slotEnd(LocalDateTime.now().plusDays(2).plusHours(1))
                .mode(InterviewMode.VIDEO)
                .locationOrLink("http://zoom.us")
                .build();

        mockMvc.perform(post("/api/applications/" + applicationAId + "/interviews")
                .header("Authorization", recruiterBToken) // WRONG RECRUITER
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(intReq)))
                .andExpect(status().isForbidden());
    }

    /**
     * Test constraint: Cannot schedule interview in the past.
     */
    @Test
    void testConstraint_CannotScheduleInterviewInPast() throws Exception {
        InterviewRequestDTO intReq = InterviewRequestDTO.builder()
                .slotStart(LocalDateTime.now().minusDays(1)) // PAST
                .slotEnd(LocalDateTime.now().minusDays(1).plusHours(1))
                .mode(InterviewMode.VIDEO)
                .locationOrLink("http://zoom.us")
                .build();

        mockMvc.perform(post("/api/applications/" + applicationAId + "/interviews")
                .header("Authorization", recruiterAToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(intReq)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test status update security: Seeker B cannot accept Seeker A's interview.
     */
    @Test
    void testSecurity_SeekerB_CannotRespondTo_SeekerA_Interview() throws Exception {
        // 1. Recruiter A schedules
        InterviewRequestDTO intReq = InterviewRequestDTO.builder()
                .slotStart(LocalDateTime.now().plusDays(5))
                .slotEnd(LocalDateTime.now().plusDays(5).plusHours(1))
                .mode(InterviewMode.TELEPHONY)
                .locationOrLink("123-456")
                .build();

        String resp = mockMvc.perform(post("/api/applications/" + applicationAId + "/interviews")
                .header("Authorization", recruiterAToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(intReq)))
                .andReturn().getResponse().getContentAsString();
        String interviewId = objectMapper.readTree(resp).get("id").asText();

        // 2. Seeker B tries to accept
        mockMvc.perform(patch("/api/interviews/" + interviewId + "/status")
                .header("Authorization", seekerBToken) // WRONG SEEKER
                .param("status", "ACCEPTED"))
                .andExpect(status().isForbidden());
    }

    /**
     * Test Seeker dashboard dynamic stats.
     */
    @Test
    void testDashboard_SeekerStats_MixedStatus() throws Exception {
        // Apply to a second job
        JobRequestDTO job2 = JobRequestDTO.builder().title("Frontend Dev").description("Desc 2").isPublished(true)
                .build();
        String job2Resp = mockMvc
                .perform(post("/api/companies/" + companyAId + "/jobs").header("Authorization", recruiterAToken)
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(job2)))
                .andReturn().getResponse().getContentAsString();
        String job2Id = objectMapper.readTree(job2Resp).get("id").asText();

        ApplicationRequestDTO app2Req = ApplicationRequestDTO.builder().resumeUrl("url").build();
        String app2Resp = mockMvc
                .perform(post("/api/jobs/" + job2Id + "/apply").header("Authorization", seekerAToken)
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(app2Req)))
                .andReturn().getResponse().getContentAsString();
        String app2Id = objectMapper.readTree(app2Resp).get("id").asText();

        // Change Status of app2 to OFFERED (using recruiter A)
        StatusUpdateRequestDTO update = new StatusUpdateRequestDTO();
        update.setStatus(ApplicationStatus.OFFERED);
        mockMvc.perform(patch("/api/applications/" + app2Id + "/status")
                .header("Authorization", recruiterAToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk());

        // Check Seeker A Dashboard
        mockMvc.perform(get("/api/dashboards/seeker").header("Authorization", seekerAToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalApplications").value(2))
                .andExpect(jsonPath("$.offerCount").value(1));
    }

    /**
     * Test Company dashboard stats.
     */
    @Test
    void testDashboard_CompanyStats() throws Exception {
        mockMvc.perform(get("/api/dashboards/company").header("Authorization", recruiterAToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalJobsPosted").value(1))
                .andExpect(jsonPath("$.totalApplicants").value(1));
    }
}
