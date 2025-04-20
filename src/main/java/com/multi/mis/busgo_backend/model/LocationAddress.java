package com.multi.mis.busgo_backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "location_addresses")
public class LocationAddress {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;
    
    private String addressLine1;
    private String addressLine2;
    private String zipCode;
    private String landmark;
    private Double latitude;
    private Double longitude;
    
    @OneToOne
    @JoinColumn(name = "location_id")
    private BusLocation location;
    
    // Default constructor
    public LocationAddress() {
    }
    
    // Constructor with fields
    public LocationAddress(String addressLine1, String addressLine2, String zipCode, 
                          String landmark, Double latitude, Double longitude) {
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.zipCode = zipCode;
        this.landmark = landmark;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    
    // Getters and Setters
    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public BusLocation getLocation() {
        return location;
    }

    public void setLocation(BusLocation location) {
        this.location = location;
    }
} 