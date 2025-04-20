package com.multi.mis.busgo_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.multi.mis.busgo_backend.model.BusCompany;

@Repository
public interface BusCompanyRepository extends JpaRepository<BusCompany, Long> {
    List<BusCompany> findByCompanyNameContainingIgnoreCase(String companyName);
    List<BusCompany> findByActive(boolean active);
    Optional<BusCompany> findByContactEmail(String email);
} 