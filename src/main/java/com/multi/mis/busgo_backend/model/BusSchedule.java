package com.multi.mis.busgo_backend.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "bus_schedules")
public class BusSchedule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduleId;
    
    @ManyToOne
    @JoinColumn(name = "company_id")
    private BusCompany company;
    
    @ManyToOne
    @JoinColumn(name = "route_id")
    private Route route;
    
    @ManyToOne
    @JoinColumn(name = "source_location_id")
    private BusLocation sourceLocation;
    
    @ManyToOne
    @JoinColumn(name = "destination_location_id")
    private BusLocation destinationLocation;
    
    private Date departureTime;
    private Date arrivalTime;
    private Double fare;
    private String busType; // AC, Non-AC, Sleeper, etc.
    private Integer totalSeats;
    private Integer availableSeats;
    private String busNumber;
    private boolean active;
    
    // Default constructor
    public BusSchedule() {
    }
    
    // Constructor with fields
    public BusSchedule(BusCompany company, Route route, BusLocation sourceLocation, BusLocation destinationLocation,
                      Date departureTime, Date arrivalTime, Double fare, String busType,
                      Integer totalSeats, Integer availableSeats, String busNumber, boolean active) {
        this.company = company;
        this.route = route;
        this.sourceLocation = sourceLocation;
        this.destinationLocation = destinationLocation;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.fare = fare;
        this.busType = busType;
        this.totalSeats = totalSeats;
        this.availableSeats = availableSeats;
        this.busNumber = busNumber;
        this.active = active;
    }
    
    // Getters and Setters
    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }
    
    // Method to get ID that matches other entity conventions
    public Long getId() {
        return scheduleId;
    }

    public BusCompany getCompany() {
        return company;
    }

    public void setCompany(BusCompany company) {
        this.company = company;
    }
    
    public Route getRoute() {
        return route;
    }
    
    public void setRoute(Route route) {
        this.route = route;
    }

    public BusLocation getSourceLocation() {
        return sourceLocation;
    }

    public void setSourceLocation(BusLocation sourceLocation) {
        this.sourceLocation = sourceLocation;
    }

    public BusLocation getDestinationLocation() {
        return destinationLocation;
    }

    public void setDestinationLocation(BusLocation destinationLocation) {
        this.destinationLocation = destinationLocation;
    }

    public Date getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Date departureTime) {
        this.departureTime = departureTime;
    }

    public Date getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Date arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public Double getFare() {
        return fare;
    }

    public void setFare(Double fare) {
        this.fare = fare;
    }

    public String getBusType() {
        return busType;
    }

    public void setBusType(String busType) {
        this.busType = busType;
    }

    public Integer getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(Integer totalSeats) {
        this.totalSeats = totalSeats;
    }

    public Integer getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(Integer availableSeats) {
        this.availableSeats = availableSeats;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(String busNumber) {
        this.busNumber = busNumber;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
} 