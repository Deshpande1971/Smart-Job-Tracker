package com.jobtracker.repository;

import com.jobtracker.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, UUID> {

    boolean existsByJobIdAndApplicantId(UUID jobId, UUID applicantId);

    List<JobApplication> findByApplicantId(UUID applicantId);

    List<JobApplication> findByJobId(UUID jobId);

    // Check if the application belongs to a company via Job link
    List<JobApplication> findByJobCompanyId(UUID companyId);
}
