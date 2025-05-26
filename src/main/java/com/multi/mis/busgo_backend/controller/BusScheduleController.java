package com.multi.mis.busgo_backend.controller;

import com.multi.mis.busgo_backend.model.*;
import com.multi.mis.busgo_backend.service.BusScheduleService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/schedules")
public class BusScheduleController {

    @Autowired
    private BusScheduleService busScheduleService;

    @PostMapping
    public ResponseEntity<?> createBusSchedule(@RequestBody Map<String, Object> requestMap,
                                               HttpServletRequest request) {
        UserDetails userDetails = (UserDetails) request.getAttribute("user"); // Standard UserDetails
        BusCompany busCompany = (BusCompany) request.getAttribute("busCompany"); // Specific BusCompany object

        // If userDetails is null, it implies the user is not authenticated for this request.
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication required to access this resource.");
        }

        // Correctly check if the user has the "ROLE_ADMIN" authority
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(grantedAuthority -> "ROLE_ADMIN".equals(grantedAuthority.getAuthority()));

        // The current logic denies access if:
        // 1. The authenticated entity is NOT a BusCompany (busCompany is null), OR
        // 2. The authenticated entity IS an Admin (isAdmin is true).
        // This effectively restricts the endpoint to non-Admin BusCompany users.
        if (busCompany == null || isAdmin) {
            // HttpStatus.FORBIDDEN (403) is more appropriate than UNAUTHORIZED (401)
            // when a user is authenticated but lacks the necessary permissions for a resource.
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                 .body("Access denied. This action is restricted to authorized non-admin company accounts.");
        }

        try {
            System.out.println("Received request to /api/schedules");
            System.out.println("Request data: " + requestMap);

            // Extract fields
            @SuppressWarnings("unchecked")
            Map<String, Object> companyMap = (Map<String, Object>) requestMap.get("company");
            @SuppressWarnings("unchecked")
            Map<String, Object> routeMap = (Map<String, Object>) requestMap.get("route");
            @SuppressWarnings("unchecked")
            Map<String, Object> sourceLocationMap = (Map<String, Object>) requestMap.get("sourceLocation");
            @SuppressWarnings("unchecked")
            Map<String, Object> destinationLocationMap = (Map<String, Object>) requestMap.get("destinationLocation");
            String departureTimeStr = (String) requestMap.get("departureTime");
            String arrivalTimeStr = (String) requestMap.get("arrivalTime");
            Object fareObj = requestMap.get("fare");
            String busType = (String) requestMap.get("busType");
            Object totalSeatsObj = requestMap.get("totalSeats");
            Object availableSeatsObj = requestMap.get("availableSeats");
            String busNumber = (String) requestMap.get("busNumber");
            Boolean active = (Boolean) requestMap.get("active");

            // Validate required fields
            if (companyMap == null || routeMap == null || sourceLocationMap == null || destinationLocationMap == null ||
                    departureTimeStr == null || arrivalTimeStr == null || fareObj == null ||
                    totalSeatsObj == null || availableSeatsObj == null || busNumber == null) {
                return ResponseEntity.badRequest().body(
                        Collections.singletonMap("error", "All required fields must be provided"));
            }

            // Extract IDs
            Object companyIdObj = companyMap.get("companyId");
            Object routeIdObj = routeMap.get("routeId");
            Object sourceLocationIdObj = sourceLocationMap.get("locationId");
            Object destinationLocationIdObj = destinationLocationMap.get("locationId");

            if (companyIdObj == null || routeIdObj == null || sourceLocationIdObj == null || destinationLocationIdObj == null) {
                return ResponseEntity.badRequest().body(
                        Collections.singletonMap("error", "Company ID, Route ID, Source Location ID, and Destination Location ID are required"));
            }

            Long companyId, routeId, sourceLocationId, destinationLocationId;
            try {
                companyId = Long.valueOf(companyIdObj.toString());
                routeId = Long.valueOf(routeIdObj.toString());
                sourceLocationId = Long.valueOf(sourceLocationIdObj.toString());
                destinationLocationId = Long.valueOf(destinationLocationIdObj.toString());
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body(
                        Collections.singletonMap("error", "Invalid ID format"));
            }

            // Parse dates in local time format: yyyy-MM-dd'T'HH:mm:ss
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            dateFormat.setLenient(false);
            Date departureTime, arrivalTime;
            try {
                departureTime = dateFormat.parse(departureTimeStr);
                arrivalTime = dateFormat.parse(arrivalTimeStr);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(
                        Collections.singletonMap("error", "Invalid date format. Expected yyyy-MM-dd'T'HH:mm:ss"));
            }

            // Validate arrivalTime is after departureTime
            if (!arrivalTime.after(departureTime)) {
                return ResponseEntity.badRequest().body(
                        Collections.singletonMap("error", "Arrival time must be after departure time"));
            }

            // Convert numeric fields
            Double fare;
            Integer totalSeats, availableSeats;
            try {
                fare = Double.valueOf(fareObj.toString());
                totalSeats = Integer.valueOf(totalSeatsObj.toString());
                availableSeats = Integer.valueOf(availableSeatsObj.toString());
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body(
                        Collections.singletonMap("error", "Invalid number format for fare, total seats, or available seats"));
            }

            // Validate relationships
            Optional<BusCompany> companyOptional = busScheduleService.getBusCompanyRepository().findById(companyId);
            if (!companyOptional.isPresent()) {
                return ResponseEntity.badRequest().body(
                        Collections.singletonMap("error", "Company with ID " + companyId + " not found"));
            }
            Optional<Route> routeOptional = busScheduleService.getRouteRepository().findById(routeId);
            if (!routeOptional.isPresent()) {
                return ResponseEntity.badRequest().body(
                        Collections.singletonMap("error", "Route with ID " + routeId + " not found"));
            }
            Optional<BusLocation> sourceLocationOptional = busScheduleService.getBusLocationRepository().findById(sourceLocationId);
            if (!sourceLocationOptional.isPresent()) {
                return ResponseEntity.badRequest().body(
                        Collections.singletonMap("error", "Source Location with ID " + sourceLocationId + " not found"));
            }
            Optional<BusLocation> destinationLocationOptional = busScheduleService.getBusLocationRepository().findById(destinationLocationId);
            if (!destinationLocationOptional.isPresent()) {
                return ResponseEntity.badRequest().body(
                        Collections.singletonMap("error", "Destination Location with ID " + destinationLocationId + " not found"));
            }

            // Create BusSchedule object
            BusSchedule schedule = new BusSchedule();
            schedule.setCompany(companyOptional.get());
            schedule.setRoute(routeOptional.get());
            schedule.setSourceLocation(sourceLocationOptional.get());
            schedule.setDestinationLocation(destinationLocationOptional.get());
            schedule.setDepartureTime(departureTime);
            schedule.setArrivalTime(arrivalTime);
            schedule.setFare(fare);
            schedule.setBusType(busType != null ? busType : "Standard");
            schedule.setTotalSeats(totalSeats);
            schedule.setAvailableSeats(availableSeats);
            schedule.setBusNumber(busNumber);
            schedule.setActive(active != null ? active : true);

            BusSchedule createdSchedule = busScheduleService.createSchedule(schedule);
            System.out.println("Schedule created: " + createdSchedule.getScheduleId());
            return ResponseEntity.ok(createdSchedule);
        } catch (Exception e) {
            System.out.println("Error creating schedule: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Collections.singletonMap("error", "Error creating schedule: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<BusSchedule>> getBusSchedules() {
        return ResponseEntity.ok(busScheduleService.getAllSchedules());
    }

    @GetMapping("/search")
    public ResponseEntity<List<BusSchedule>> searchBus(
            @RequestParam Long sourceId,
            @RequestParam Long destId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date departureDate) {
        return ResponseEntity.ok(busScheduleService.searchBus(sourceId, destId, departureDate));
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<BusSchedule>> getBusScheduleByCompany(@PathVariable Long companyId) {
        return ResponseEntity.ok(busScheduleService.getBusScheduleByCompany(companyId));
    }

    @GetMapping("/search-by-city")
    public ResponseEntity<List<BusSchedule>> searchBusByCity(
            @RequestParam String sourceCity,
            @RequestParam String destCity,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date departureDate) {
        return ResponseEntity.ok(busScheduleService.searchBusByCity(sourceCity, destCity, departureDate));
    }

    @GetMapping("/{scheduleId}")
    public ResponseEntity<?> getBusScheduleById(@PathVariable Long scheduleId) {
        return busScheduleService.getScheduleById(scheduleId)
                .map(schedule -> ResponseEntity.ok(schedule))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{scheduleId}")
    public ResponseEntity<?> updateBusSchedule(
            @PathVariable Long scheduleId,
            @RequestBody BusSchedule schedule) {
        return busScheduleService.updateSchedule(scheduleId, schedule)
                .map(updatedSchedule -> ResponseEntity.ok(updatedSchedule))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<?> deleteBusSchedule(@PathVariable Long scheduleId) {
        boolean deleted = busScheduleService.deleteSchedule(scheduleId);
        if (deleted) {
            return ResponseEntity.ok(Collections.singletonMap("message", "Schedule deleted successfully"));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
