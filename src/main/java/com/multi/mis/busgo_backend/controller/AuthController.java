package com.multi.mis.busgo_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.multi.mis.busgo_backend.dto.LoginRequest;
import com.multi.mis.busgo_backend.dto.LoginResponse;
import com.multi.mis.busgo_backend.model.BusCompany;
import com.multi.mis.busgo_backend.model.User;
import com.multi.mis.busgo_backend.security.JwtUtil;
import com.multi.mis.busgo_backend.service.BusCompanyService;
import com.multi.mis.busgo_backend.service.UserService;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private BusCompanyService busCompanyService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            final UserDetails userDetails = userService.loadUserByUsername(loginRequest.getEmail());
            final String jwt = jwtUtil.generateToken(userDetails);

            return ResponseEntity.ok(new LoginResponse(jwt, "USER"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid email or password");
        }
    }

    @PostMapping("/company/login")
    public ResponseEntity<?> companyLogin(@RequestBody LoginRequest loginRequest) {
        try {
            BusCompany company = busCompanyService.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new Exception("Company not found"));

            if (!passwordEncoder.matches(loginRequest.getPassword(), company.getPassword())) {
                throw new Exception("Invalid password");
            }

            final String jwt = jwtUtil.generateToken(company.getContactEmail());

            return ResponseEntity.ok(new LoginResponse(jwt, "COMPANY", company));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid company credentials");
        }
    }

    // Fixed the endpoint mapping - removed the duplicate "/auth" prefix
    @PostMapping("/admin/login")
    public ResponseEntity<?> adminLogin(@RequestBody LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            final UserDetails userDetails = userService.loadUserByUsername(loginRequest.getEmail());
            final String jwt = jwtUtil.generateToken(userDetails);

            return ResponseEntity.ok(new LoginResponse(jwt, "ADMIN"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid email or password");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, Object> requestMap) {
        try {
            System.out.println("Received request to /api/auth/register");
            System.out.println("Request data: " + requestMap);

            // Manually extract fields from the request
            String username = (String) requestMap.get("username");
            String email = (String) requestMap.get("email");
            String password = (String) requestMap.get("password");
            String role = (String) requestMap.get("role");
            String firstName = (String) requestMap.get("firstName");
            String lastName = (String) requestMap.get("lastName");
            String phoneNumber = (String) requestMap.get("phoneNumber");

            // Handle isActive - could be sent as is_active or isActive
            Boolean isActive = null;
            if (requestMap.containsKey("isActive")) {
                isActive = (Boolean) requestMap.get("isActive");
            } else if (requestMap.containsKey("is_active")) {
                isActive = (Boolean) requestMap.get("is_active");
            }

            // Validate required fields
            if (username == null || email == null || password == null) {
                return ResponseEntity.badRequest().body(
                        java.util.Collections.singletonMap("message", "Username, email, and password are required")
                );
            }

            System.out.println("Registering user: " + username + ", " + email);

            // Check for duplicate username
            if (userService.findByUsername(username) != null) {
                return ResponseEntity.badRequest().body(
                        java.util.Collections.singletonMap("message", "Username already exists")
                );
            }

            // Check for duplicate email
            if (userService.findByEmail(email) != null) {
                return ResponseEntity.badRequest().body(
                        java.util.Collections.singletonMap("message", "Email already exists")
                );
            }

            // Create a new User object
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(role != null ? role : "USER"); // Default to USER if not specified
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setPhoneNumber(phoneNumber);
            user.setActive(isActive != null ? isActive : true); // Default to true if not specified

            User savedUser = userService.createUser(user);
            System.out.println("User registered: " + savedUser.getUsername());

            // Generate JWT token for the newly registered user
            final String jwt = jwtUtil.generateToken(savedUser.getUsername());

            // Return the same response format as login
            return ResponseEntity.ok(new LoginResponse(jwt, savedUser.getRole(), savedUser));
        } catch (Exception e) {
            System.out.println("Error registering user: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(
                    java.util.Collections.singletonMap("message", "Error registering user: " + e.getMessage())
            );
        }
    }
}