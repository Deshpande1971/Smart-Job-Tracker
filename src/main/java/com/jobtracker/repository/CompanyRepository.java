package com.jobtracker.repository;

import com.jobtracker.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompanyRepository extends JpaRepository<Company, UUID> {
    Optional<Company> findBySlug(String slug);

    boolean existsByName(String name);

    boolean existsByContactEmail(String contactEmail);
}
