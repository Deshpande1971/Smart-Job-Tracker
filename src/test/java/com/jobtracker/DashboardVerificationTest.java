package com.jobtracker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobtracker.dto.*;
import com.jobtracker.entity.*;
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
public class DashboardVerificationTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        void testDashboards() throws Exception {
                // 1. Setup Recruiter & Seeker
                String recToken = "Bearer "
                                + registerAndGetToken("Recruiter", "r" + UUID.randomUUID() + "@t.com",
                                                UserRole.COMPANY);
                String seekToken = "Bearer "
                                + registerAndGetToken("Seeker", "s" + UUID.randomUUID() + "@t.com",
                                                UserRole.JOB_SEEKER);

                // 2. Company & Job
                CompanyRequestDTO compReq = CompanyRequestDTO.builder().name("Dash Corp")
                                .description("Min twenty chars for desc").industry("Fin").contactEmail("f@f.com")
                                .build();
                String compResp = mockMvc
                                .perform(post("/api/companies").header("Authorization", recToken)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(compReq)))
                                .andReturn().getResponse().getContentAsString();
                String companyId = objectMapper.readTree(compResp).get("id").asText();

                JobRequestDTO jobReq = JobRequestDTO.builder().title("Analyst").description("Desc").location("NY")
                                .employmentType(EmploymentType.FULL_TIME).isPublished(true).build();
                mockMvc.perform(post("/api/companies/" + companyId + "/jobs").header("Authorization", recToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(jobReq)))
                                .andExpect(status().isOk());

                // 3. Application
                JobRequestDTO job2 = JobRequestDTO.builder().title("Analyst 2").description("Desc").location("NY")
                                .employmentType(EmploymentType.FULL_TIME).isPublished(true).build();
                String job2Resp = mockMvc
                                .perform(post("/api/companies/" + companyId + "/jobs").header("Authorization", recToken)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsString(job2)))
                                .andReturn().getResponse().getContentAsString();
                String job2Id = objectMapper.readTree(job2Resp).get("id").asText();

                ApplicationRequestDTO appReq = ApplicationRequestDTO.builder().resumeUrl("http://res.pdf").build();
                mockMvc.perform(post("/api/jobs/" + job2Id + "/apply").header("Authorization", seekToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(appReq)))
                                .andExpect(status().isOk());

                // 4. Seeker Dashboard
                mockMvc.perform(get("/api/dashboards/seeker").header("Authorization", seekToken))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.totalApplications").value(1));

                // 5. Company Dashboard
                mockMvc.perform(get("/api/dashboards/company").header("Authorization", recToken))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.totalJobsPosted").value(2))
                                .andExpect(jsonPath("$.totalApplicants").value(1));
        }

        private String registerAndGetToken(String name, String email, UserRole role) throws Exception {
                RegistrationRequestDTO reg = RegistrationRequestDTO.builder().name(name).email(email)
                                .password("password123")
                                .role(role).build();
                String resp = mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(reg))).andReturn().getResponse()
                                .getContentAsString();
                return objectMapper.readValue(resp, AuthResponseDTO.class).getToken();
        }
}
