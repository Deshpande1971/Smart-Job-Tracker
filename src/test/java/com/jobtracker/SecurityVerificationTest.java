package com.jobtracker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobtracker.dto.AuthRequestDTO;
import com.jobtracker.dto.RegistrationRequestDTO;
import com.jobtracker.entity.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class SecurityVerificationTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        @DisplayName("Should register a new Job Seeker successfully")
        void registerJobSeeker() throws Exception {
                RegistrationRequestDTO request = RegistrationRequestDTO.builder()
                                .name("Test Seeker")
                                .email("seeker@test.com")
                                .password("password123")
                                .role(UserRole.JOB_SEEKER)
                                .build();

                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.token").exists())
                                .andExpect(jsonPath("$.email").value("seeker@test.com"))
                                .andExpect(jsonPath("$.role").value("JOB_SEEKER"));
        }

        @Test
        @DisplayName("Should Fail: Register with duplicate email")
        void registerDuplicateEmail() throws Exception {
                RegistrationRequestDTO request = RegistrationRequestDTO.builder()
                                .name("User 1")
                                .email("duplicate@test.com")
                                .password("password123")
                                .role(UserRole.JOB_SEEKER)
                                .build();

                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)));

                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("Should Fail: Register with invalid email format")
        void registerInvalidEmail() throws Exception {
                RegistrationRequestDTO request = RegistrationRequestDTO.builder()
                                .name("Invalid User")
                                .email("not-an-email")
                                .password("password123")
                                .role(UserRole.JOB_SEEKER)
                                .build();

                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should Fail: Login with wrong password")
        void loginWrongPassword() throws Exception {
                RegistrationRequestDTO reg = RegistrationRequestDTO.builder()
                                .name("Alex")
                                .email("alex@test.com")
                                .password("correctPassword")
                                .role(UserRole.JOB_SEEKER)
                                .build();

                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(reg)));

                AuthRequestDTO login = AuthRequestDTO.builder()
                                .email("alex@test.com")
                                .password("wrongPassword")
                                .build();

                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(login)))
                                .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("Should Fail: Access protected endpoint without token")
        void accessProtectedEndpoint() throws Exception {
                mockMvc.perform(post("/api/some-protected-action"))
                                .andExpect(status().isForbidden());
        }
}
