package com.multi.mis.busgo_backend.service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.multi.mis.busgo_backend.model.Bus;
import com.multi.mis.busgo_backend.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.multi.mis.busgo_backend.model.BusCompany;
import com.multi.mis.busgo_backend.repository.BusCompanyRepository;
import java.util.logging.Logger;

@Service
public class BusCompanyService implements UserDetailsService {

    private static  final Logger logger = Logger.getLogger(BusCompanyService.class.getName());
    
    @Autowired
    private BusCompanyRepository busCompanyRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public List<BusCompany> getAllCompanies() {
        return busCompanyRepository.findAll();
    }
    
    public Optional<BusCompany> getCompanyById(Long id) {
        return busCompanyRepository.findById(id);
    }
    public Optional<BusCompany> login(String email, String password) {
        Optional<BusCompany> company = busCompanyRepository.findByContactEmail(email);
        if (company.isPresent() && passwordEncoder.matches(password, company.get().getPassword())) {
            return company;
        }
        return Optional.empty();
    }
    /**
     * Find a bus company by its contact email
     *
     * @param email The contact email to search for
     * @return The bus company if found, null otherwise
     */
    public BusCompany findByContactEmail(String email) {
        logger.info("Finding company by email: " + email);
        if (email == null || email.isEmpty()) {
            logger.warning("Attempted to find company with null or empty email");
            return null;
        }

        return busCompanyRepository.findByContactEmail(email).orElse(null);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.info("Loading company by username/email: " + email);

        // First try to find by email
        Optional<BusCompany> companyOpt = busCompanyRepository.findByContactEmail(email);

        if (companyOpt.isEmpty()) {
            logger.warning("Company not found with email: " + email);
            throw new UsernameNotFoundException("Company not found with email: " + email);
        }

        BusCompany company = companyOpt.get();
        logger.info("Company found: " + company.getCompanyName() + " with email: " + company.getContactEmail());


        // Create and return Spring Security UserDetails object
        return new org.springframework.security.core.userdetails.User(
                company.getContactEmail(), // Use contact email as the username for authentication
                company.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_COMPANY"))
        );
    }



    //search
    public List<BusCompany> searchCompanies(String name) {
        return busCompanyRepository.findByCompanyNameContainingIgnoreCase(name);
    }
    public List<BusCompany> searchCompaniesByActiveStatus(boolean active) {
        return busCompanyRepository.findByActive(active);
    }
    public Optional<BusCompany> findById(Long id) {
        return busCompanyRepository.findById(id);
    }
    
    public BusCompany createCompany(BusCompany company) {
        company.setRegistrationDate(new Date());
        company.setActive(true);
        company.setPassword(passwordEncoder.encode(company.getPassword()));
        return busCompanyRepository.save(company);
    }
    public Optional<BusCompany> updateCompanyStatus(Long id, boolean active) {
        return busCompanyRepository.findById(id)
                .map(existingCompany -> {
                    existingCompany.setActive(active);
                    return busCompanyRepository.save(existingCompany);
                });
    }


    
    public Optional<BusCompany> updateCompany(Long id, BusCompany updatedCompany) {
        return busCompanyRepository.findById(id)
                .map(existingCompany -> {
                    if (updatedCompany.getCompanyName() != null) {
                        existingCompany.setCompanyName(updatedCompany.getCompanyName());
                    }
                    if (updatedCompany.getContactPerson() != null) {
                        existingCompany.setContactPerson(updatedCompany.getContactPerson());
                    }
                    if (updatedCompany.getContactEmail() != null) {
                        existingCompany.setContactEmail(updatedCompany.getContactEmail());
                    }
                    if (updatedCompany.getContactPhone() != null) {
                        existingCompany.setContactPhone(updatedCompany.getContactPhone());
                    }
                    if (updatedCompany.getAddress() != null) {
                        existingCompany.setAddress(updatedCompany.getAddress());
                    }
                    if (updatedCompany.getLicenseNumber() != null) {
                        existingCompany.setLicenseNumber(updatedCompany.getLicenseNumber());
                    }
                    if (updatedCompany.getPassword() != null) {
                        existingCompany.setPassword(passwordEncoder.encode(updatedCompany.getPassword()));
                    }
                    return busCompanyRepository.save(existingCompany);
                });
    }
    
    public boolean deleteCompany(Long id) {
        if (busCompanyRepository.existsById(id)) {
            busCompanyRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    public List<BusCompany> findByCompanyName(String name) {
        return busCompanyRepository.findByCompanyNameContainingIgnoreCase(name);
    }
    
    public List<BusCompany> findActiveCompanies() {
        return busCompanyRepository.findByActive(true);
    }

    public Optional<BusCompany> findByEmail(String email) {
        return busCompanyRepository.findByContactEmail(email);
    }
} 