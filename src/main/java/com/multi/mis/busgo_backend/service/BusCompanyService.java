package com.multi.mis.busgo_backend.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.multi.mis.busgo_backend.model.BusCompany;
import com.multi.mis.busgo_backend.repository.BusCompanyRepository;

@Service
public class BusCompanyService {
    
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