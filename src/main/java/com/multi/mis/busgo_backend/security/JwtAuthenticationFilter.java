package com.multi.mis.busgo_backend.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.multi.mis.busgo_backend.model.BusCompany;
import com.multi.mis.busgo_backend.service.BusCompanyService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final BusCompanyService busCompanyService;

    private static final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
            "/api/auth/register",
            "/api/auth/login",
            "/api/auth/company/login",
            "/api/auth/admin/login",
            " /api/schedules/search",
            "/api/schedules",
            "/api/companies/login",
            "/api/auth/request-password-reset",
            "/api/auth/reset-password",
            "/api/test/public",
            "/error"
    );

    public JwtAuthenticationFilter(
            JwtUtil jwtUtil,
            @Qualifier("userService") UserDetailsService userDetailsService,
            BusCompanyService busCompanyService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.busCompanyService = busCompanyService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        boolean shouldSkip = PUBLIC_ENDPOINTS.contains(path) || request.getMethod().equals("OPTIONS");
        if (shouldSkip) {
            logger.debug("Skipping JWT authentication for: {} Method: {}", path, request.getMethod());
        }
        return shouldSkip;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String path = request.getServletPath();
        logger.debug("Processing request: {}", path);

        final String authorizationHeader = request.getHeader("Authorization");

        String email = null;
        String jwt = null;
        String role = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                email = jwtUtil.extractUsername(jwt);
                role = jwtUtil.extractRole(jwt);
                logger.debug("Extracted from token - Email: {}, Role: {}", email, role);
            } catch (Exception e) {
                logger.error("Error extracting information from token: {}", e.getMessage());
            }
        } else {
            logger.debug("No Authorization header or invalid format for path: {}", path);
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails;

                // Choose the appropriate service based on the role in the token
                if (role != null && role.equals("COMPANY")) {
                    logger.debug("Loading company UserDetails for: {}", email);
                    userDetails = busCompanyService.loadUserByUsername(email);
                } else {
                    logger.debug("Loading user UserDetails for: {}", email);
                    userDetails = userDetailsService.loadUserByUsername(email);
                }

                if (jwtUtil.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    
                    // Add the UserDetails object to the request attributes
                    request.setAttribute("user", userDetails);
                    logger.debug("Authenticated (UserDetails): {} with role: {}. UserDetails object added to request attributes as 'user'.", userDetails.getUsername(), role);

                    // If it's a company, also add the BusCompany object
                    if (role != null && role.equals("COMPANY")) {
                        BusCompany company = busCompanyService.findByContactEmail(email); // email is the username here
                        if (company != null) {
                            request.setAttribute("busCompany", company);
                            logger.debug("Additionally, BusCompany object for {} (ID: {}) added to request attributes as 'busCompany'.", company.getCompanyName(), company.getCompanyId());
                        } else {
                            // This case should ideally not happen if loadUserByUsername succeeded for a company
                            logger.warn("Could not retrieve BusCompany object for email {} even though UserDetails were loaded for COMPANY role. 'busCompany' attribute not set.", email);
                        }
                    }
                    logger.info("Authentication successful for entity: {} with role: {} for path: {}", userDetails.getUsername(), role, path);
                } else {
                    logger.warn("Invalid JWT token for email: {}", email);
                }
            } catch (Exception e) {
                // Log the error with more context, including the email if available
                String errorMsg = "Error during authentication process";
                if (email != null) {
                    errorMsg += " for email " + email;
                }
                logger.error(errorMsg + ": {}", e.getMessage(), e);
            }
        }

        chain.doFilter(request, response);
    }
}
