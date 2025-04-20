package com.multi.mis.busgo_backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "bus_locations")
public class BusLocation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long locationId;
    
    private String locationName;
    private String city;
    private String state;
    private String country;
    private String locationType; // Bus Terminal, Bus Stop, etc.
    
    @OneToOne(mappedBy = "location", cascade = CascadeType.ALL)
    private LocationAddress address;
    
    // Default constructor
    public BusLocation() {
    }
    
    // Constructor with fields
    public BusLocation(String locationName, String city, String state, String country, String locationType) {
        this.locationName = locationName;
        this.city = city;
        this.state = state;
        this.country = country;
        this.locationType = locationType;
    }
    
    // Getters and Setters
    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLocationType() {
        return locationType;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    public LocationAddress getAddress() {
        return address;
    }

    public void setAddress(LocationAddress address) {
        this.address = address;
    }
} 