package com.multi.mis.busgo_backend.controller;

import com.multi.mis.busgo_backend.model.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Debug controller to help diagnose route creation issues
 * This is a temporary controller for debugging purposes only
 */
@RestController
@RequestMapping("/api/debug/routes")
public class RouteDebugController {

    private static final Logger logger = LoggerFactory.getLogger(RouteDebugController.class);

    @PostMapping
    public ResponseEntity<?> debugRouteCreation(@RequestBody Route route) {
        logger.info("Debug route creation request received");
        logger.info("Route: {}", route);

        // Log detailed information about the company object
        if (route.getCompany() == null) {
            logger.error("Company object is null");
            return ResponseEntity.badRequest().body("Company object is null");
        }

        logger.info("Company: {}", route.getCompany());
        logger.info("Company ID via getId(): {}", route.getCompany().getId());
        logger.info("Company ID via getCompanyId(): {}", route.getCompany().getCompanyId());

        // Return the received data for verification
        return ResponseEntity.ok(route);
    }
}