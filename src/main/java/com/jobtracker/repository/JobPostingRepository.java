package com.jobtracker.repository;

import com.jobtracker.entity.JobPosting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JobPostingRepository extends JpaRepository<JobPosting, UUID> {
    List<JobPosting> findByCompanyId(UUID companyId);

    List<JobPosting> findByIsPublishedTrue();
}
