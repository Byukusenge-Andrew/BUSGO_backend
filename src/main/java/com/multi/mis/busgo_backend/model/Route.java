package com.multi.mis.busgo_backend.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "routes")
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long routeId;

    private String routeName;
    private String routeCode;
    private String description;
    private double totalDistance; // in kilometers
    private int estimatedDuration; // in minutes
    private double basePrice; // Added basePrice field
    private boolean active;
    private String origin;
    private String destination;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private BusCompany company;

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL)
    private List<RouteStop> stops;

    // Default constructor
    public Route() {
    }

    // Constructor with fields
    public Route(String routeName, String routeCode, String description,
                 double totalDistance, int estimatedDuration, double basePrice,
                 boolean active, String origin, String destination, BusCompany company) {
        this.routeName = routeName;
        this.routeCode = routeCode;
        this.description = description;
        this.totalDistance = totalDistance;
        this.estimatedDuration = estimatedDuration;
        this.basePrice = basePrice;
        this.active = active;
        this.origin = origin;
        this.destination = destination;
        this.company = company;
    }

    // Getters and Setters
    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getRouteCode() {
        return routeCode;
    }

    public void setRouteCode(String routeCode) {
        this.routeCode = routeCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public int getEstimatedDuration() {
        return estimatedDuration;
    }

    public void setEstimatedDuration(int estimatedDuration) {
        this.estimatedDuration = estimatedDuration;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public BusCompany getCompany() {
        return company;
    }

    public void setCompany(BusCompany company) {
        this.company = company;
    }

    public List<RouteStop> getStops() {
        return stops;
    }

    public void setStops(List<RouteStop> stops) {
        this.stops = stops;
    }

    // Alias methods to maintain compatibility with service layer
    public Long getId() {
        return this.routeId;
    }

    public String getName() {
        return this.routeName;
    }

    public Long getCompanyId() {
        return company != null ? company.getCompanyId() : null;
    }
}