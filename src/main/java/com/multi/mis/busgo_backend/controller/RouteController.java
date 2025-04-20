package com.multi.mis.busgo_backend.controller;

import com.multi.mis.busgo_backend.model.Route;
import com.multi.mis.busgo_backend.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/routes")
@CrossOrigin(origins = "*")
public class RouteController {

    @Autowired
    private RouteService routeService;
    
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
    
    @PostMapping
    public ResponseEntity<Route> createRoute(@RequestBody Route route) {
        return ResponseEntity.ok(routeService.createRoute(route));
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