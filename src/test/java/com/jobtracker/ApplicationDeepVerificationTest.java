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
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ApplicationDeepVerificationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String seekerToken;
    private String recruiterToken;
    private String companyId;
    private String publishedJobId;
    private String unpublishedJobId;

    @BeforeEach
    void setUp() throws Exception {
        // Setup tokens and entities
        recruiterToken = "Bearer "
                + registerAndGetToken("Recruiter", "rec_" + UUID.randomUUID() + "@test.com", UserRole.COMPANY);
        seekerToken = "Bearer "
                + registerAndGetToken("Seeker", "seek_" + UUID.randomUUID() + "@test.com", UserRole.JOB_SEEKER);

        // Create Company
        CompanyRequestDTO compReq = CompanyRequestDTO.builder()
                .name("Deep Test Corp " + UUID.randomUUID())
                .description("Verification of complex business rules for Phase 4.")
                .industry("Quality Assurance")
                .contactEmail("qa_" + UUID.randomUUID() + "@test.com")
                .build();
        String compResp = mockMvc.perform(post("/api/companies").header("Authorization", recruiterToken)
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(compReq)))
                .andReturn().getResponse().getContentAsString();
        companyId = objectMapper.readTree(compResp).get("id").asText();

        // Create Published Job
        publishedJobId = createJob("Senior Dev", true);
        // Create Unpublished Job
        unpublishedJobId = createJob("Junior Dev", false);
    }

    private String registerAndGetToken(String name, String email, UserRole role) throws Exception {
        RegistrationRequestDTO reg = RegistrationRequestDTO.builder()
                .name(name).email(email).password("password123").role(role).build();
        String resp = mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reg))).andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(resp, AuthResponseDTO.class).getToken();
    }

    private String createJob(String title, boolean published) throws Exception {
        JobRequestDTO jobReq = JobRequestDTO.builder()
                .title(title).description("Details").location("Remote")
                .employmentType(EmploymentType.FULL_TIME).isPublished(published).build();
        String resp = mockMvc
                .perform(post("/api/companies/" + companyId + "/jobs").header("Authorization", recruiterToken)
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(jobReq)))
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(resp).get("id").asText();
    }

    @Test
    void testConstraint_CannotApplyToUnpublishedJob() throws Exception {
        ApplicationRequestDTO req = ApplicationRequestDTO.builder().resumeUrl("http://resume.com").build();

        mockMvc.perform(post("/api/jobs/" + unpublishedJobId + "/apply")
                .header("Authorization", seekerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Cannot apply to an unpublished job"));
    }

    @Test
    void testConstraint_ValidationMissingResume() throws Exception {
        ApplicationRequestDTO req = ApplicationRequestDTO.builder().resumeUrl("").build(); // Blank

        mockMvc.perform(post("/api/jobs/" + publishedJobId + "/apply")
                .header("Authorization", seekerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resumeUrl").exists());
    }

    @Test
    void testSecurity_SeekerCannotUpdateStatus() throws Exception {
        // 1. Seeker applies
        ApplicationRequestDTO req = ApplicationRequestDTO.builder().resumeUrl("http://res.com").build();
        String resp = mockMvc.perform(post("/api/jobs/" + publishedJobId + "/apply")
                .header("Authorization", seekerToken).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))).andReturn().getResponse().getContentAsString();
        String appId = objectMapper.readTree(resp).get("id").asText();

        // 2. Seeker tries to update own status to SHORTLISTED
        StatusUpdateRequestDTO update = StatusUpdateRequestDTO.builder().status(ApplicationStatus.SHORTLISTED).build();
        mockMvc.perform(patch("/api/applications/" + appId + "/status")
                .header("Authorization", seekerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testSecurity_CrossRecruiterAppListing() throws Exception {
        // Recruiter B should not see applications for Recruiter A's job if we enforce
        // it.
        // Current implementation returns list. Let's see if we should protect it.

        String recBEmail = "rec_b_" + UUID.randomUUID() + "@test.com";
        String recBToken = "Bearer " + registerAndGetToken("Recruiter B", recBEmail, UserRole.COMPANY);

        mockMvc.perform(get("/api/jobs/" + publishedJobId + "/applications")
                .header("Authorization", recBToken))
                .andExpect(status().isForbidden()); // Expected if we add authorization
    }

    @Test
    void testLifecycle_FullStatusTransition() throws Exception {
        // 1. Seeker applies
        ApplicationRequestDTO req = ApplicationRequestDTO.builder().resumeUrl("http://res.com").build();
        String resp = mockMvc.perform(post("/api/jobs/" + publishedJobId + "/apply")
                .header("Authorization", seekerToken).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))).andReturn().getResponse().getContentAsString();
        String appId = objectMapper.readTree(resp).get("id").asText();

        // 2. Recruiter: APPLIED -> SHORTLISTED
        updateStatus(appId, ApplicationStatus.SHORTLISTED);
        // 3. Recruiter: SHORTLISTED -> INTERVIEW_SCHEDULED
        updateStatus(appId, ApplicationStatus.INTERVIEW_SCHEDULED);
        // 4. Recruiter: INTERVIEW_SCHEDULED -> OFFERED
        updateStatus(appId, ApplicationStatus.OFFERED);

        // Verify final status
        mockMvc.perform(get("/api/applications/my").header("Authorization", seekerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("OFFERED"));
    }

    private void updateStatus(String appId, ApplicationStatus status) throws Exception {
        StatusUpdateRequestDTO update = StatusUpdateRequestDTO.builder().status(status).notes("Moving to " + status)
                .build();
        mockMvc.perform(patch("/api/applications/" + appId + "/status")
                .header("Authorization", recruiterToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk());
    }
}
