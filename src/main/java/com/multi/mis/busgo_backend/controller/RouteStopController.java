package com.multi.mis.busgo_backend.controller;

import com.multi.mis.busgo_backend.model.RouteStop;
import com.multi.mis.busgo_backend.service.RouteStopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/stops")
@CrossOrigin(origins = "*")
public class RouteStopController {

    @Autowired
    private RouteStopService routeStopService;
    
    @GetMapping
    public ResponseEntity<List<RouteStop>> getAllStops() {
        return ResponseEntity.ok(routeStopService.getAllStops());
    }
    
    @GetMapping("/{stopId}")
    public ResponseEntity<?> getStopById(@PathVariable Long stopId) {
        return routeStopService.getStopById(stopId)
                .map(stop -> ResponseEntity.ok(stop))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<RouteStop> createStop(@RequestBody RouteStop stop) {
        return ResponseEntity.ok(routeStopService.createStop(stop));
    }
    
    @PutMapping("/{stopId}")
    public ResponseEntity<?> updateStop(
            @PathVariable Long stopId, 
            @RequestBody RouteStop stop) {
        return routeStopService.updateStop(stopId, stop)
                .map(updatedStop -> ResponseEntity.ok(updatedStop))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{stopId}")
    public ResponseEntity<?> deleteStop(@PathVariable Long stopId) {
        boolean deleted = routeStopService.deleteStop(stopId);
        
        if (deleted) {
            return ResponseEntity.ok("Stop deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/route/{routeId}")
    public ResponseEntity<List<RouteStop>> getStopsByRoute(@PathVariable Long routeId) {
        return ResponseEntity.ok(routeStopService.getStopsByRoute(routeId));
    }
    
    @GetMapping("/location/{locationId}")
    public ResponseEntity<List<RouteStop>> getStopsByLocation(@PathVariable Long locationId) {
        return ResponseEntity.ok(routeStopService.getStopsByLocation(locationId));
    }
    
    @GetMapping("/route/{routeId}/pickup-points")
    public ResponseEntity<List<RouteStop>> getPickupPointsByRoute(@PathVariable Long routeId) {
        return ResponseEntity.ok(routeStopService.getPickupPointsByRoute(routeId));
    }
    
    @GetMapping("/route/{routeId}/drop-points")
    public ResponseEntity<List<RouteStop>> getDropPointsByRoute(@PathVariable Long routeId) {
        return ResponseEntity.ok(routeStopService.getDropPointsByRoute(routeId));
    }
} 