package com.multi.mis.busgo_backend.service;

import com.multi.mis.busgo_backend.model.Route;
import com.multi.mis.busgo_backend.repository.RouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RouteService {
    
    @Autowired
    private RouteRepository routeRepository;
    
    public List<Route> getAllRoutes() {
        return routeRepository.findAll();
    }
    
    public Optional<Route> getRouteById(Long id) {
        return routeRepository.findById(id);
    }
    
    public Route createRoute(Route route) {
        route.setActive(true);
        return routeRepository.save(route);
    }
    
    public Optional<Route> updateRoute(Long id, Route updatedRoute) {
        return routeRepository.findById(id)
                .map(existingRoute -> {
                    if (updatedRoute.getRouteName() != null) {
                        existingRoute.setRouteName(updatedRoute.getRouteName());
                    }
                    if (updatedRoute.getRouteCode() != null) {
                        existingRoute.setRouteCode(updatedRoute.getRouteCode());
                    }
                    if (updatedRoute.getDescription() != null) {
                        existingRoute.setDescription(updatedRoute.getDescription());
                    }
                    if (updatedRoute.getTotalDistance() != 0) {
                        existingRoute.setTotalDistance(updatedRoute.getTotalDistance());
                    }
                    if (updatedRoute.getEstimatedDuration() != 0) {
                        existingRoute.setEstimatedDuration(updatedRoute.getEstimatedDuration());
                    }
                    if (updatedRoute.getCompany() != null) {
                        existingRoute.setCompany(updatedRoute.getCompany());
                    }
                    return routeRepository.save(existingRoute);
                });
    }
    
    public boolean deleteRoute(Long id) {
        if (routeRepository.existsById(id)) {
            routeRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    public List<Route> getRoutesByCompany(Long companyId) {
        return routeRepository.findByCompanyCompanyId(companyId);
    }
    
    public List<Route> getActiveRoutes() {
        return routeRepository.findByActive(true);
    }
    
    public List<Route> searchRoutesByName(String name) {
        return routeRepository.findByRouteNameContainingIgnoreCase(name);
    }
    
    public Optional<Route> getRouteByCode(String code) {
        List<Route> routes = routeRepository.findByRouteCode(code);
        return routes.isEmpty() ? Optional.empty() : Optional.of(routes.get(0));
    }
} 