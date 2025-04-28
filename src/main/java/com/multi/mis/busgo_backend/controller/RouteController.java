package com.multi.mis.busgo_backend.controller;

import com.multi.mis.busgo_backend.model.BusCompany;
import com.multi.mis.busgo_backend.model.Route;
import com.multi.mis.busgo_backend.repository.BusCompanyRepository;
import com.multi.mis.busgo_backend.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

    @Autowired
    private RouteService routeService;



    @PostMapping
    public ResponseEntity<?> createRoute(@RequestBody Map<String, Object> requestMap) {
        try {
            System.out.println("Received request to /api/routes");
            System.out.println("Request data: " + requestMap);

            // Extract fields from the request
            String routeName = (String) requestMap.get("routeName");
            String routeCode = (String) requestMap.get("routeCode");
            String description = (String) requestMap.get("description");
            Object totalDistanceObj = requestMap.get("totalDistance");
            Object estimatedDurationObj = requestMap.get("estimatedDuration");
            Object basePriceObj = requestMap.get("basePrice");
            Boolean active = (Boolean) requestMap.get("active");
            String origin = (String) requestMap.get("origin");
            String destination = (String) requestMap.get("destination");
            @SuppressWarnings("unchecked")
            Map<String, Object> companyMap = (Map<String, Object>) requestMap.get("company");

            // Validate required fields
            if (routeName == null || routeCode == null || totalDistanceObj == null ||
                    estimatedDurationObj == null || origin == null || destination == null || companyMap == null) {
                return ResponseEntity.badRequest().body(
                        Collections.singletonMap("error", "Route name, code, distance, duration, origin, destination, and company are required"));
            }

            // Extract companyId
            Object companyIdObj = companyMap.get("companyId");
            if (companyIdObj == null) {
                return ResponseEntity.badRequest().body(
                        Collections.singletonMap("error", "Company ID is required"));
            }

            Long companyId;
            try {
                companyId = Long.valueOf(companyIdObj.toString());
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body(
                        Collections.singletonMap("error", "Invalid company ID format"));
            }

            // Convert numeric fields
            Double totalDistance;
            Integer estimatedDuration;
            Double basePrice;
            try {
                totalDistance = Double.valueOf(totalDistanceObj.toString());
                estimatedDuration = Integer.valueOf(estimatedDurationObj.toString());
                basePrice = basePriceObj != null ? Double.valueOf(basePriceObj.toString()) : 0.0;
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body(
                        Collections.singletonMap("error", "Invalid format for distance, duration, or base price"));
            }

            // Validate company exists
            Optional<BusCompany> companyOptional = routeService.getBusCompanyRepository().findById(companyId);
            if (!companyOptional.isPresent()) {
                return ResponseEntity.badRequest().body(
                        Collections.singletonMap("error", "Company with ID " + companyId + " not found"));
            }

            // Create Route object
            Route route = new Route();
            route.setRouteName(routeName);
            route.setRouteCode(routeCode);
            route.setDescription(description != null ? description : "");
            route.setTotalDistance(totalDistance);
            route.setEstimatedDuration(estimatedDuration);
            route.setBasePrice(basePrice);
            route.setActive(active != null ? active : true);
            route.setOrigin(origin);
            route.setDestination(destination);
            route.setCompany(companyOptional.get());

            // Save the route
            Route createdRoute = routeService.createRoute(route);
            System.out.println("Route created: " + createdRoute.getRouteName());

            return ResponseEntity.ok(createdRoute);
        } catch (Exception e) {
            System.out.println("Error creating route: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(
                    Collections.singletonMap("error", "Error creating route: " + e.getMessage()));
        }
    }

    // Other methods remain unchanged
    @GetMapping
    public ResponseEntity<List<Route>> getAllRoutes() {
        return ResponseEntity.ok(routeService.getAllRoutes());
    }

    @GetMapping("/{routeId}")
    public ResponseEntity<?> getRouteById(@PathVariable Long routeId) {
        return routeService.getRouteById(routeId)
                .map(route -> ResponseEntity.ok(route))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{routeId}")
    public ResponseEntity<?> updateRoute(
            @PathVariable Long routeId,
            @RequestBody Route route) {
        return routeService.updateRoute(routeId, route)
                .map(updatedRoute -> ResponseEntity.ok(updatedRoute))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{routeId}")
    public ResponseEntity<?> deleteRoute(@PathVariable Long routeId) {
        boolean deleted = routeService.deleteRoute(routeId);
        if (deleted) {
            return ResponseEntity.ok("Route deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<Route>> getRoutesByCompany(@PathVariable Long companyId) {
        return ResponseEntity.ok(routeService.getRoutesByCompany(companyId));
    }

    @GetMapping("/active")
    public ResponseEntity<List<Route>> getActiveRoutes() {
        return ResponseEntity.ok(routeService.getActiveRoutes());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Route>> searchRoutesByName(@RequestParam String name) {
        return ResponseEntity.ok(routeService.searchRoutesByName(name));
    }

    @GetMapping("/by-code/{code}")
    public ResponseEntity<?> getRouteByCode(@PathVariable String code) {
        return routeService.getRouteByCode(code)
                .map(route -> ResponseEntity.ok(route))
                .orElse(ResponseEntity.notFound().build());
    }
}