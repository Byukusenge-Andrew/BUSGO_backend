package com.multi.mis.busgo_backend.controller;

import com.multi.mis.busgo_backend.model.Bus;
import com.multi.mis.busgo_backend.service.BusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/buses")
@CrossOrigin(origins = "*")
public class BusController {
    @Autowired
    private BusService busService;

    // Get all buses
    @GetMapping
    public ResponseEntity<List<Bus>> getAllBuses() {
        return ResponseEntity.ok(busService.getAllBuses());
    }

    // Get bus by ID
    @GetMapping("/{id}")
    public ResponseEntity<Bus> getBusById(@PathVariable Long id) {
        return busService.getBusById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Get buses by company ID
    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<Bus>> getBusesByCompany(@PathVariable String companyId) {
        return ResponseEntity.ok(busService.getBusesByCompany(companyId));
    }

    // Get buses by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Bus>> getBusesByStatus(@PathVariable String status) {
        return ResponseEntity.ok(busService.getBusesByStatus(status));
    }

    // Get buses by company ID and status
    @GetMapping("/company/{companyId}/status/{status}")
    public ResponseEntity<List<Bus>> getBusesByCompanyAndStatus(
            @PathVariable String companyId, @PathVariable String status) {
        return ResponseEntity.ok(busService.getBusesByCompanyAndStatus(companyId, status));
    }

    // Create a new bus
    @PostMapping
    public ResponseEntity<Bus> createBus(@RequestBody Bus bus) {
        // Log the received bus data for debugging
        System.out.println("Received bus data: " + bus.getRegistrationNumber() + ", Type: " + bus.getBusType());

        Bus savedBus = busService.saveBus(bus);
        return new ResponseEntity<>(savedBus, HttpStatus.CREATED);
    }

    // Update a bus
    @PutMapping("/{id}")
    public ResponseEntity<Bus> updateBus(@PathVariable Long id, @RequestBody Bus bus) {
        return busService.getBusById(id)
                .map(existingBus -> {
                    // Update fields from the request
                    if (bus.getRegistrationNumber() != null) {
                        existingBus.setRegistrationNumber(bus.getRegistrationNumber());
                    }
                    if (bus.getModel() != null) {
                        existingBus.setModel(bus.getModel());
                    }
                    if (bus.getCapacity() != null) {
                        existingBus.setCapacity(bus.getCapacity());
                    }
                    if (bus.getBusType() != null) {
                        existingBus.setBusType(bus.getBusType());
                    }
                    if (bus.getCompanyId() != null) {
                        existingBus.setCompanyId(bus.getCompanyId());
                    }
                    if (bus.getStatus() != null) {
                        existingBus.setStatus(bus.getStatus());
                    }
                    if (bus.getFeatures() != null) {
                        existingBus.setFeatures(bus.getFeatures());
                    }
                    if (bus.getNotes() != null) {
                        existingBus.setNotes(bus.getNotes());
                    }
                    return ResponseEntity.ok(busService.saveBus(existingBus));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Update bus status
    @PatchMapping("/{id}/status")
    public ResponseEntity<Bus> updateBusStatus(
            @PathVariable Long id, @RequestBody Map<String, String> statusUpdate) {
        String status = statusUpdate.get("status");
        if (status == null) {
            return ResponseEntity.badRequest().build();
        }
        return busService.updateBusStatus(id, status)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete a bus
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBus(@PathVariable Long id) {
        if (!busService.getBusById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        busService.deleteBus(id);
        return ResponseEntity.noContent().build();
    }
}