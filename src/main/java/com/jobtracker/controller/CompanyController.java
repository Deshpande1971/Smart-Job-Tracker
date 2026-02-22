package com.jobtracker.controller;

import com.jobtracker.dto.CompanyRequestDTO;
import com.jobtracker.dto.CompanyResponseDTO;
import com.jobtracker.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for Company management.
 */
@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    // Only COMPANY role can register a new company profile.
    @PostMapping
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<CompanyResponseDTO> createCompany(@Valid @RequestBody CompanyRequestDTO request) {
        return ResponseEntity.ok(companyService.createCompany(request));
    }

    // Public endpoint to view all companies.
    @GetMapping
    public ResponseEntity<List<CompanyResponseDTO>> getAllCompanies() {
        return ResponseEntity.ok(companyService.getAllCompanies());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyResponseDTO> getCompanyById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(companyService.getCompanyById(id));
    }

    // Only ADMIN can verify a company.
    @PatchMapping("/{id}/verify")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CompanyResponseDTO> verifyCompany(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(companyService.verifyCompany(id));
    }

    @PatchMapping("/{id}/suspend")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CompanyResponseDTO> suspendCompany(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(companyService.suspendCompany(id));
    }

    @PatchMapping("/{id}/reinstate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CompanyResponseDTO> reinstateCompany(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(companyService.reinstateCompany(id));
    }
}
