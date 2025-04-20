package com.multi.mis.busgo_backend.service;

import com.multi.mis.busgo_backend.model.RouteStop;
import com.multi.mis.busgo_backend.repository.RouteStopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RouteStopService {
    
    @Autowired
    private RouteStopRepository routeStopRepository;
    
    public List<RouteStop> getAllStops() {
        return routeStopRepository.findAll();
    }
    
    public Optional<RouteStop> getStopById(Long id) {
        return routeStopRepository.findById(id);
    }
    
    public RouteStop createStop(RouteStop stop) {
        return routeStopRepository.save(stop);
    }
    
    public Optional<RouteStop> updateStop(Long id, RouteStop updatedStop) {
        return routeStopRepository.findById(id)
                .map(existingStop -> {
                    if (updatedStop.getRoute() != null) {
                        existingStop.setRoute(updatedStop.getRoute());
                    }
                    if (updatedStop.getLocation() != null) {
                        existingStop.setLocation(updatedStop.getLocation());
                    }
                    if (updatedStop.getSequenceNumber() != 0) {
                        existingStop.setSequenceNumber(updatedStop.getSequenceNumber());
                    }
                    if (updatedStop.getDistanceFromStart() != 0) {
                        existingStop.setDistanceFromStart(updatedStop.getDistanceFromStart());
                    }
                    if (updatedStop.getEstimatedTimeFromStart() != 0) {
                        existingStop.setEstimatedTimeFromStart(updatedStop.getEstimatedTimeFromStart());
                    }
                    if (updatedStop.getFareFromStart() != 0) {
                        existingStop.setFareFromStart(updatedStop.getFareFromStart());
                    }
                    existingStop.setPickupPoint(updatedStop.isPickupPoint());
                    existingStop.setDropPoint(updatedStop.isDropPoint());
                    return routeStopRepository.save(existingStop);
                });
    }
    
    public boolean deleteStop(Long id) {
        if (routeStopRepository.existsById(id)) {
            routeStopRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    public List<RouteStop> getStopsByRoute(Long routeId) {
        return routeStopRepository.findByRouteRouteIdOrderBySequenceNumberAsc(routeId);
    }
    
    public List<RouteStop> getStopsByLocation(Long locationId) {
        return routeStopRepository.findByLocationLocationId(locationId);
    }
    
    public List<RouteStop> getPickupPointsByRoute(Long routeId) {
        return routeStopRepository.findByRouteRouteIdAndIsPickupPointTrue(routeId);
    }
    
    public List<RouteStop> getDropPointsByRoute(Long routeId) {
        return routeStopRepository.findByRouteRouteIdAndIsDropPointTrue(routeId);
    }
} 