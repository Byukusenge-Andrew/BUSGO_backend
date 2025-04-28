package com.multi.mis.busgo_backend.service;

import com.multi.mis.busgo_backend.model.BusCompany;
import com.multi.mis.busgo_backend.model.Route;
import com.multi.mis.busgo_backend.repository.BusCompanyRepository;
import com.multi.mis.busgo_backend.repository.RouteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RouteService {

    private static final Logger logger = LoggerFactory.getLogger(RouteService.class);

    @Autowired
    private RouteRepository routeRepository;
    @Autowired
    private BusCompanyRepository busCompanyRepository;

    public BusCompanyRepository getBusCompanyRepository() {
        return busCompanyRepository;
    }
        @Transactional
    public Route createRoute(Route route) {
        logger.debug("Creating route: {}", route);

        // Set route as active by default if not specified
        if (!route.isActive()) {
            route.setActive(true);
        }

        // Validate company object
        if (route.getCompany() == null) {
            logger.error("Company object is null in the request");
            throw new IllegalArgumentException("Company ID is required");
        }

        // Expect companyId field in the company object
        Long companyId = route.getCompany().getCompanyId();
        if (companyId == null) {
            logger.error("Company ID is null or not provided");
            throw new IllegalArgumentException("Company ID is required");
        }

        // Look up the company
        Optional<BusCompany> companyOptional = busCompanyRepository.findById(companyId);
        if (companyOptional.isPresent()) {
            route.setCompany(companyOptional.get());
            logger.debug("Found company: {}", companyOptional.get().getCompanyName());
        } else {
            logger.error("Company with ID {} not found", companyId);
            throw new IllegalArgumentException("Company with ID " + companyId + " not found");
        }

        logger.debug("Saving route: {}", route);
        return routeRepository.save(route);
    }

    // Update handleCompanyReference for consistency
    private void handleCompanyReference(Route route) {
        if (route.getCompany() != null) {
            Long companyId = route.getCompany().getCompanyId();
            if (companyId != null) {
                Optional<BusCompany> existingCompany = busCompanyRepository.findById(companyId);
                if (existingCompany.isPresent()) {
                    route.setCompany(existingCompany.get());
                } else {
                    throw new IllegalArgumentException("Referenced company with ID " + companyId + " does not exist");
                }
            } else {
                throw new IllegalArgumentException("Company ID is required");
            }
        }
    }

    // Other methods remain unchanged
    public List<Route> getAllRoutes() {
        return routeRepository.findAll();
    }

    public Optional<Route> getRouteById(Long id) {
        return routeRepository.findById(id);
    }

    @Transactional
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
                        handleCompanyReference(updatedRoute);
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