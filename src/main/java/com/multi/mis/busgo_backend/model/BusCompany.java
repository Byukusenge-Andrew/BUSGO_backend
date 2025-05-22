package com.multi.mis.busgo_backend.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;

@Entity
@Table(name = "bus_companies")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BusCompany {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long companyId;
    @Column(nullable = false)
    private String companyName;
    private String contactPerson;
    private String contactEmail;
    private String contactPhone;
    private String address;
    private String licenseNumber ;
    private String password;
    private Date registrationDate;
    private boolean active;
    
    
    // Default constructor
    public BusCompany() {
    }
    
    // Constructor with fields
    public BusCompany(String companyName, String contactPerson, String contactEmail, 
                    String contactPhone, String address, String licenseNumber,
                    Date registrationDate, boolean active) {
        this.companyName = companyName;
        this.contactPerson = contactPerson;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
        this.address = address;
        this.licenseNumber = licenseNumber;
        this.registrationDate = registrationDate;
        this.active = active;
    }
    
    // Getters and Setters
    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
    
    // Alias method for compatibility with service layer
    public Long getId() {
        return companyId;
    }
} 