package com.multi.mis.busgo_backend.repository;

import java.util.List;
import java.util.Optional;

import com.multi.mis.busgo_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.multi.mis.busgo_backend.model.BusCompany;

@Repository
public interface BusCompanyRepository extends JpaRepository<BusCompany, Long> {
    List<BusCompany> findByCompanyNameContainingIgnoreCase(String companyName);
    List<BusCompany> findByActive(boolean active);
    /**
     * Find a bus company by its contact email
     *
     * @param email The contact email to search for
     * @return An Optional containing the bus company if found
     */
    Optional<BusCompany> findByContactEmail(String email);

//    Optional<User> findByEmail(String usernameOrEmail);
//
//    User findByUsername(String usernameOrEmail);
}