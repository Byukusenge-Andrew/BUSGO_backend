package com.multi.mis.busgo_backend.controller;

import com.multi.mis.busgo_backend.model.BusLocation;
import com.multi.mis.busgo_backend.model.LocationAddress;
import com.multi.mis.busgo_backend.service.BusLocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/locations")
public class BusLocationController {

    @Autowired
    private BusLocationService busLocationService;
    
    @GetMapping
    public ResponseEntity<List<BusLocation>> getBusLocations() {
        return ResponseEntity.ok(busLocationService.getAllLocations());
    }
    
    @GetMapping("/{locationId}")
    public ResponseEntity<?> getBusLocationById(@PathVariable Long locationId) {
        return busLocationService.getLocationById(locationId)
                .map(location -> ResponseEntity.ok(location))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{locationId}/address")
    public ResponseEntity<?> getAddressByLocationId(@PathVariable Long locationId) {
        return busLocationService.getAddressByLocationId(locationId)
                .map(address -> ResponseEntity.ok(address))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/{locationId}/address")
    public ResponseEntity<LocationAddress> createOrUpdateLocationAddress(
            @PathVariable Long locationId, 
            @RequestBody LocationAddress address) {
        return ResponseEntity.ok(busLocationService.createOrUpdateLocationAddress(locationId, address));
    }
    
    @PutMapping("/{locationId}")
    public ResponseEntity<?> updateBusLocation(
            @PathVariable Long locationId, 
            @RequestBody BusLocation location) {
        return busLocationService.updateLocation(locationId, location)
                .map(updatedLocation -> ResponseEntity.ok(updatedLocation))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<BusLocation> createBusLocation(@RequestBody BusLocation location) {
        return ResponseEntity.ok(busLocationService.createLocation(location));
    }
    
    @DeleteMapping("/{locationId}")
    public ResponseEntity<?> deleteBusLocation(@PathVariable Long locationId) {
        boolean deleted = busLocationService.deleteLocation(locationId);
        
        if (deleted) {
            return ResponseEntity.ok("Location deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
} 