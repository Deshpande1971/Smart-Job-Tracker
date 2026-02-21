package com.jobtracker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobtracker.dto.AuthResponseDTO;
import com.jobtracker.dto.CompanyRequestDTO;
import com.jobtracker.dto.JobRequestDTO;
import com.jobtracker.dto.RegistrationRequestDTO;
import com.jobtracker.entity.UserRole;
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
public class CompanyJobEdgeCaseTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        /**
         * Helper to get a token for a given role.
         */
        private String getAuthToken(String email, UserRole role) throws Exception {
                RegistrationRequestDTO reg = RegistrationRequestDTO.builder()
                                .name("Test User")
                                .email(email)
                                .password("password123")
                                .role(role)
                                .build();

                String response = mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(reg)))
                                .andExpect(status().isOk())
                                .andReturn().getResponse().getContentAsString();

                return "Bearer " + objectMapper.readValue(response, AuthResponseDTO.class).getToken();
        }

        @Test
        void testCompanyValidation_ShortDescription() throws Exception {
                String token = getAuthToken("recruiter_v1_" + UUID.randomUUID() + "@test.com", UserRole.COMPANY);

                CompanyRequestDTO invalidComp = CompanyRequestDTO.builder()
                                .name("Short Desc Co " + UUID.randomUUID())
                                .description("Too short") // < 20 chars
                                .industry("Tech")
                                .contactEmail("short_" + UUID.randomUUID() + "@test.com")
                                .build();

                mockMvc.perform(post("/api/companies")
                                .header("Authorization", token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidComp)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.description").exists());
        }

        @Test
        void testDuplicateCompanyRegistration() throws Exception {
                String token = getAuthToken("recruiter_d1_" + UUID.randomUUID() + "@test.com", UserRole.COMPANY);
                String name = "Duplicate Corp " + UUID.randomUUID();

                CompanyRequestDTO comp = CompanyRequestDTO.builder()
                                .name(name)
                                .description("Valid description that is long enough.")
                                .industry("Tech")
                                .contactEmail("unique_" + UUID.randomUUID() + "@test.com")
                                .build();

                // 1st time - Success
                mockMvc.perform(post("/api/companies")
                                .header("Authorization", token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(comp)))
                                .andExpect(status().isOk());

                // 2nd time - Failure (Duplicate Name) returns 400 Bad Request now
                mockMvc.perform(post("/api/companies")
                                .header("Authorization", token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(comp)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message").value("Company name already exists"));
        }

        @Test
        void testRoleMismatch_SeekerCannotCreateCompany() throws Exception {
                String seekerToken = getAuthToken("seeker_r1_" + UUID.randomUUID() + "@test.com", UserRole.JOB_SEEKER);

                CompanyRequestDTO comp = CompanyRequestDTO.builder()
                                .name("Seeker Corp " + UUID.randomUUID())
                                .description("Valid description that is long enough.")
                                .industry("Tech")
                                .contactEmail("seeker_corp_" + UUID.randomUUID() + "@test.com")
                                .build();

                mockMvc.perform(post("/api/companies")
                                .header("Authorization", seekerToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(comp)))
                                .andExpect(status().isForbidden());
        }

        @Test
        void testRecruiterAuthorization_CrossPostingFailure() throws Exception {
                // Recruiter A creates Company A
                String tokenA = getAuthToken("recruiter_a_" + UUID.randomUUID() + "@test.com", UserRole.COMPANY);
                CompanyRequestDTO compA = CompanyRequestDTO.builder()
                                .name("Company A " + UUID.randomUUID())
                                .description("Valid description that is long enough for A.")
                                .industry("Tech")
                                .contactEmail("hr_a_" + UUID.randomUUID() + "@test.com")
                                .build();

                String responseA = mockMvc.perform(post("/api/companies")
                                .header("Authorization", tokenA)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(compA)))
                                .andExpect(status().isOk())
                                .andReturn().getResponse().getContentAsString();
                String idA = objectMapper.readTree(responseA).get("id").asText();

                // Recruiter B tries to post a job for Company A
                String tokenB = getAuthToken("recruiter_b_" + UUID.randomUUID() + "@test.com", UserRole.COMPANY);
                JobRequestDTO job = JobRequestDTO.builder()
                                .title("Unauthorized Job")
                                .description("Should fail.")
                                .build();

                mockMvc.perform(post("/api/companies/" + idA + "/jobs")
                                .header("Authorization", tokenB)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(job)))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.message")
                                                .value("Unauthorized: You do not belong to this company"));
        }

        @Test
        void testGetNonExistentCompany() throws Exception {
                mockMvc.perform(get("/api/companies/" + UUID.randomUUID()))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.message").value("Company not found"));
        }
}
