package com.jobtracker.service;

import com.jobtracker.dto.CompanyRequestDTO;
import com.jobtracker.dto.CompanyResponseDTO;
import com.jobtracker.entity.Company;
import com.jobtracker.entity.User;
import com.jobtracker.entity.UserRole;
import com.jobtracker.exception.AlreadyExistsException;
import com.jobtracker.repository.CompanyRepository;
import com.jobtracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Handles Company profile management and verification.
 */
@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    @Transactional
    public CompanyResponseDTO createCompany(CompanyRequestDTO request) {
        // 1. Validation
        if (companyRepository.existsByName(request.getName())) {
            throw new AlreadyExistsException("Company name already exists");
        }
        if (companyRepository.existsByContactEmail(request.getContactEmail())) {
            throw new AlreadyExistsException("Contact email is already registered to another company");
        }

        // 2. Map to Entity
        Company company = Company.builder()
                .name(request.getName())
                .slug(generateSlug(request.getName()))
                .description(request.getDescription())
                .industry(request.getIndustry())
                .contactEmail(request.getContactEmail())
                .companySize(request.getCompanySize())
                .headquarters(request.getHeadquarters())
                .website(request.getWebsite())
                .logoUrl(request.getLogoUrl())
                .build();

        Company savedCompany = companyRepository.save(company);

        // 3. Link current user (Recruiter) to this company
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        if (currentUser.getRole() != UserRole.COMPANY) {
            throw new RuntimeException("Only users with ROLE_COMPANY can create a company profile");
        }

        currentUser.setCompany(savedCompany);
        userRepository.save(currentUser);

        return mapToResponse(savedCompany);
    }

    public List<CompanyResponseDTO> getAllCompanies() {
        return companyRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public CompanyResponseDTO getCompanyById(UUID id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found"));
        return mapToResponse(company);
    }

    @Transactional
    public CompanyResponseDTO verifyCompany(UUID id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found"));
        company.setVerified(true);
        return mapToResponse(companyRepository.save(company));
    }

    // Helper: Build a slug from the company name
    private String generateSlug(String name) {
        return name.toLowerCase().replaceAll("[^a-z0-9]", "-").replaceAll("-+", "-");
    }

    private CompanyResponseDTO mapToResponse(Company company) {
        return CompanyResponseDTO.builder()
                .id(company.getId())
                .name(company.getName())
                .slug(company.getSlug())
                .description(company.getDescription())
                .industry(company.getIndustry())
                .contactEmail(company.getContactEmail())
                .verified(company.isVerified())
                .status(company.getStatus())
                .build();
    }
}
