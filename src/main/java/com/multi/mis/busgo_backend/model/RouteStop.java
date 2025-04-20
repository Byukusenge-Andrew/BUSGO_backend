package com.multi.mis.busgo_backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "route_stops")
public class RouteStop {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stopId;
    
    @ManyToOne
    @JoinColumn(name = "route_id")
    private Route route;
    
    @ManyToOne
    @JoinColumn(name = "location_id")
    private BusLocation location;
    
    private int sequenceNumber;
    private double distanceFromStart; // in kilometers
    private int estimatedTimeFromStart; // in minutes
    private double fareFromStart; // fare from the starting point
    private boolean isPickupPoint;
    private boolean isDropPoint;
    
    // Default constructor
    public RouteStop() {
    }
    
    // Constructor with fields
    public RouteStop(Route route, BusLocation location, int sequenceNumber,
                    double distanceFromStart, int estimatedTimeFromStart,
                    double fareFromStart, boolean isPickupPoint, boolean isDropPoint) {
        this.route = route;
        this.location = location;
        this.sequenceNumber = sequenceNumber;
        this.distanceFromStart = distanceFromStart;
        this.estimatedTimeFromStart = estimatedTimeFromStart;
        this.fareFromStart = fareFromStart;
        this.isPickupPoint = isPickupPoint;
        this.isDropPoint = isDropPoint;
    }
    
    // Getters and Setters
    public Long getStopId() {
        return stopId;
    }

    public void setStopId(Long stopId) {
        this.stopId = stopId;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public BusLocation getLocation() {
        return location;
    }

    public void setLocation(BusLocation location) {
        this.location = location;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public double getDistanceFromStart() {
        return distanceFromStart;
    }

    public void setDistanceFromStart(double distanceFromStart) {
        this.distanceFromStart = distanceFromStart;
    }

    public int getEstimatedTimeFromStart() {
        return estimatedTimeFromStart;
    }

    public void setEstimatedTimeFromStart(int estimatedTimeFromStart) {
        this.estimatedTimeFromStart = estimatedTimeFromStart;
    }

    public double getFareFromStart() {
        return fareFromStart;
    }

    public void setFareFromStart(double fareFromStart) {
        this.fareFromStart = fareFromStart;
    }

    public boolean isPickupPoint() {
        return isPickupPoint;
    }

    public void setPickupPoint(boolean isPickupPoint) {
        this.isPickupPoint = isPickupPoint;
    }

    public boolean isDropPoint() {
        return isDropPoint;
    }

    public void setDropPoint(boolean isDropPoint) {
        this.isDropPoint = isDropPoint;
    }
} 