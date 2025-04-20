package com.multi.mis.busgo_backend.controller;

import com.multi.mis.busgo_backend.model.*;
import com.multi.mis.busgo_backend.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/BusBooking")
@CrossOrigin(origins = "*")
public class BusBookingController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private BusCompanyService busCompanyService;
    
    @Autowired
    private BusLocationService busLocationService;
    
    @Autowired
    private BusScheduleService busScheduleService;
    
    @Autowired
    private BusBookingService busBookingService;
    
    @Autowired
    private RouteService routeService;
    
    @Autowired
    private RouteStopService routeStopService;
    
    @Autowired
    private PaymentService paymentService;
    
    // User endpoints
    @GetMapping("/GetAllUsers")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
    
    @GetMapping("/GetUsersByRole")
    public ResponseEntity<List<User>> getUsersByRole(@RequestParam String role) {
        return ResponseEntity.ok(userService.getUsersByRole(role));
    }
    
    @GetMapping("/GetActiveUsers")
    public ResponseEntity<List<User>> getActiveUsers() {
        return ResponseEntity.ok(userService.getActiveUsers());
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        
        Optional<User> userOpt = userService.login(username, password);
        
        if (userOpt.isPresent()) {
            return ResponseEntity.ok(userOpt.get());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }
    
    @PostMapping("/AddNewUser")
    public ResponseEntity<User> addNewUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }
    
    @PostMapping("/UpdateUser")
    public ResponseEntity<?> updateUser(@RequestParam Long userId, @RequestBody User user) {
        User updatedUser = userService.updateUser(userId, user);
        return updatedUser != null 
            ? ResponseEntity.ok(updatedUser)
            : ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/DeleteUserByUserId")
    public ResponseEntity<?> deleteUser(@RequestParam Long userId) {
        boolean deleted = userService.deleteUser(userId);
        
        if (deleted) {
            return ResponseEntity.ok("User deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Company endpoints
    @PostMapping("/CreateCompany")
    public ResponseEntity<BusCompany> createCompany(@RequestBody BusCompany company) {
        return ResponseEntity.ok(busCompanyService.createCompany(company));
    }
    
    @GetMapping("/GetBusCompanies")
    public ResponseEntity<List<BusCompany>> getBusCompanies() {
        return ResponseEntity.ok(busCompanyService.getAllCompanies());
    }
    
    @GetMapping("/GetBusCompanyById")
    public ResponseEntity<?> getBusCompanyById(@RequestParam Long companyId) {
        return busCompanyService.getCompanyById(companyId)
                .map(company -> ResponseEntity.ok(company))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/PutBusCompany")
    public ResponseEntity<?> updateCompany(@RequestParam Long companyId, @RequestBody BusCompany company) {
        return busCompanyService.updateCompany(companyId, company)
                .map(updatedCompany -> ResponseEntity.ok(updatedCompany))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/PostBusCompany")
    public ResponseEntity<BusCompany> postBusCompany(@RequestBody BusCompany company) {
        return ResponseEntity.ok(busCompanyService.createCompany(company));
    }
    
    @DeleteMapping("/DeleteBusCompany")
    public ResponseEntity<?> deleteBusCompany(@RequestParam Long companyId) {
        boolean deleted = busCompanyService.deleteCompany(companyId);
        
        if (deleted) {
            return ResponseEntity.ok("Company deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Location endpoints
    @GetMapping("/GetBusLocations")
    public ResponseEntity<List<BusLocation>> getBusLocations() {
        return ResponseEntity.ok(busLocationService.getAllLocations());
    }
    
    @GetMapping("/GetBusLocationById")
    public ResponseEntity<?> getBusLocationById(@RequestParam Long locationId) {
        return busLocationService.getLocationById(locationId)
                .map(location -> ResponseEntity.ok(location))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/getAddressByLocationId")
    public ResponseEntity<?> getAddressByLocationId(@RequestParam Long locationId) {
        return busLocationService.getAddressByLocationId(locationId)
                .map(address -> ResponseEntity.ok(address))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/PostBusLocationAddress")
    public ResponseEntity<LocationAddress> postBusLocationAddress(@RequestParam Long locationId, @RequestBody LocationAddress address) {
        return ResponseEntity.ok(busLocationService.createOrUpdateLocationAddress(locationId, address));
    }
    
    @PutMapping("/PutBusLocation")
    public ResponseEntity<?> updateBusLocation(@RequestParam Long locationId, @RequestBody BusLocation location) {
        return busLocationService.updateLocation(locationId, location)
                .map(updatedLocation -> ResponseEntity.ok(updatedLocation))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/PostBusLocation")
    public ResponseEntity<BusLocation> postBusLocation(@RequestBody BusLocation location) {
        return ResponseEntity.ok(busLocationService.createLocation(location));
    }
    
    @DeleteMapping("/DeleteBusLocation")
    public ResponseEntity<?> deleteBusLocation(@RequestParam Long locationId) {
        boolean deleted = busLocationService.deleteLocation(locationId);
        
        if (deleted) {
            return ResponseEntity.ok("Location deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Schedule endpoints
    @GetMapping("/GetBusSchedules")
    public ResponseEntity<List<BusSchedule>> getBusSchedules() {
        return ResponseEntity.ok(busScheduleService.getAllSchedules());
    }
    
    @GetMapping("/searchBus")
    public ResponseEntity<List<BusSchedule>> searchBus(
            @RequestParam Long sourceId,
            @RequestParam Long destId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date departureDate) {
        return ResponseEntity.ok(busScheduleService.searchBus(sourceId, destId, departureDate));
    }
    
    @GetMapping("/getBookedSeats")
    public ResponseEntity<List<String>> getBookedSeats(@RequestParam Long scheduleId) {
        return ResponseEntity.ok(busBookingService.getBookedSeatsBySchedule(scheduleId));
    }
    
    @GetMapping("/searchBus2")
    public ResponseEntity<List<BusSchedule>> searchBus2(
            @RequestParam String sourceCity,
            @RequestParam String destCity,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date departureDate) {
        return ResponseEntity.ok(busScheduleService.searchBusByCity(sourceCity, destCity, departureDate));
    }
    
    @GetMapping("/GetBusScheduleById")
    public ResponseEntity<?> getBusScheduleById(@RequestParam Long scheduleId) {
        return busScheduleService.getScheduleById(scheduleId)
                .map(schedule -> ResponseEntity.ok(schedule))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/PutBusSchedule")
    public ResponseEntity<?> updateBusSchedule(@RequestParam Long scheduleId, @RequestBody BusSchedule schedule) {
        return busScheduleService.updateSchedule(scheduleId, schedule)
                .map(updatedSchedule -> ResponseEntity.ok(updatedSchedule))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/PostBusSchedule")
    public ResponseEntity<BusSchedule> postBusSchedule(@RequestBody BusSchedule schedule) {
        return ResponseEntity.ok(busScheduleService.createSchedule(schedule));
    }
    
    @DeleteMapping("/DeleteBusSchedule")
    public ResponseEntity<?> deleteBusSchedule(@RequestParam Long scheduleId) {
        boolean deleted = busScheduleService.deleteSchedule(scheduleId);
        
        if (deleted) {
            return ResponseEntity.ok("Schedule deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Route endpoints
    @GetMapping("/GetAllRoutes")
    public ResponseEntity<List<Route>> getAllRoutes() {
        return ResponseEntity.ok(routeService.getAllRoutes());
    }
    
    @GetMapping("/GetRouteById")
    public ResponseEntity<?> getRouteById(@RequestParam Long routeId) {
        return routeService.getRouteById(routeId)
                .map(route -> ResponseEntity.ok(route))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/CreateRoute")
    public ResponseEntity<Route> createRoute(@RequestBody Route route) {
        return ResponseEntity.ok(routeService.createRoute(route));
    }
    
    @PutMapping("/UpdateRoute")
    public ResponseEntity<?> updateRoute(@RequestParam Long routeId, @RequestBody Route route) {
        return routeService.updateRoute(routeId, route)
                .map(updatedRoute -> ResponseEntity.ok(updatedRoute))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/DeleteRoute")
    public ResponseEntity<?> deleteRoute(@RequestParam Long routeId) {
        boolean deleted = routeService.deleteRoute(routeId);
        
        if (deleted) {
            return ResponseEntity.ok("Route deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/GetRoutesByCompany")
    public ResponseEntity<List<Route>> getRoutesByCompany(@RequestParam Long companyId) {
        return ResponseEntity.ok(routeService.getRoutesByCompany(companyId));
    }
    
    @GetMapping("/GetActiveRoutes")
    public ResponseEntity<List<Route>> getActiveRoutes() {
        return ResponseEntity.ok(routeService.getActiveRoutes());
    }
    
    @GetMapping("/SearchRoutesByName")
    public ResponseEntity<List<Route>> searchRoutesByName(@RequestParam String name) {
        return ResponseEntity.ok(routeService.searchRoutesByName(name));
    }
    
    @GetMapping("/GetRouteByCode")
    public ResponseEntity<?> getRouteByCode(@RequestParam String code) {
        return routeService.getRouteByCode(code)
                .map(route -> ResponseEntity.ok(route))
                .orElse(ResponseEntity.notFound().build());
    }
    
    // RouteStop endpoints
    @GetMapping("/GetAllStops")
    public ResponseEntity<List<RouteStop>> getAllStops() {
        return ResponseEntity.ok(routeStopService.getAllStops());
    }
    
    @GetMapping("/GetStopById")
    public ResponseEntity<?> getStopById(@RequestParam Long stopId) {
        return routeStopService.getStopById(stopId)
                .map(stop -> ResponseEntity.ok(stop))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/CreateStop")
    public ResponseEntity<RouteStop> createStop(@RequestBody RouteStop stop) {
        return ResponseEntity.ok(routeStopService.createStop(stop));
    }
    
    @PutMapping("/UpdateStop")
    public ResponseEntity<?> updateStop(@RequestParam Long stopId, @RequestBody RouteStop stop) {
        return routeStopService.updateStop(stopId, stop)
                .map(updatedStop -> ResponseEntity.ok(updatedStop))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/DeleteStop")
    public ResponseEntity<?> deleteStop(@RequestParam Long stopId) {
        boolean deleted = routeStopService.deleteStop(stopId);
        
        if (deleted) {
            return ResponseEntity.ok("Stop deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/GetStopsByRoute")
    public ResponseEntity<List<RouteStop>> getStopsByRoute(@RequestParam Long routeId) {
        return ResponseEntity.ok(routeStopService.getStopsByRoute(routeId));
    }
    
    @GetMapping("/GetStopsByLocation")
    public ResponseEntity<List<RouteStop>> getStopsByLocation(@RequestParam Long locationId) {
        return ResponseEntity.ok(routeStopService.getStopsByLocation(locationId));
    }
    
    @GetMapping("/GetPickupPointsByRoute")
    public ResponseEntity<List<RouteStop>> getPickupPointsByRoute(@RequestParam Long routeId) {
        return ResponseEntity.ok(routeStopService.getPickupPointsByRoute(routeId));
    }
    
    @GetMapping("/GetDropPointsByRoute")
    public ResponseEntity<List<RouteStop>> getDropPointsByRoute(@RequestParam Long routeId) {
        return ResponseEntity.ok(routeStopService.getDropPointsByRoute(routeId));
    }
    
    // Payment endpoints
    @GetMapping("/GetAllPayments")
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }
    
    @GetMapping("/GetPaymentById")
    public ResponseEntity<?> getPaymentById(@RequestParam Long paymentId) {
        return paymentService.getPaymentById(paymentId)
                .map(payment -> ResponseEntity.ok(payment))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/CreatePayment")
    public ResponseEntity<Payment> createPayment(@RequestBody Payment payment) {
        return ResponseEntity.ok(paymentService.createPayment(payment));
    }
    
    @PutMapping("/UpdatePayment")
    public ResponseEntity<?> updatePayment(@RequestParam Long paymentId, @RequestBody Payment payment) {
        return paymentService.updatePayment(paymentId, payment)
                .map(updatedPayment -> ResponseEntity.ok(updatedPayment))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/DeletePayment")
    public ResponseEntity<?> deletePayment(@RequestParam Long paymentId) {
        boolean deleted = paymentService.deletePayment(paymentId);
        
        if (deleted) {
            return ResponseEntity.ok("Payment deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/GetPaymentsByBooking")
    public ResponseEntity<List<Payment>> getPaymentsByBooking(@RequestParam Long bookingId) {
        return ResponseEntity.ok(paymentService.getPaymentsByBooking(bookingId));
    }
    
    @GetMapping("/GetPaymentsByStatus")
    public ResponseEntity<List<Payment>> getPaymentsByStatus(@RequestParam String status) {
        return ResponseEntity.ok(paymentService.getPaymentsByStatus(status));
    }
    
    @GetMapping("/GetPaymentsByMethod")
    public ResponseEntity<List<Payment>> getPaymentsByMethod(@RequestParam String paymentMethod) {
        return ResponseEntity.ok(paymentService.getPaymentsByMethod(paymentMethod));
    }
    
    @GetMapping("/GetPaymentByTransactionId")
    public ResponseEntity<?> getPaymentByTransactionId(@RequestParam String transactionId) {
        return paymentService.getPaymentByTransactionId(transactionId)
                .map(payment -> ResponseEntity.ok(payment))
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Booking endpoints
    @GetMapping("/GetAllBusBookings")
    public ResponseEntity<List<BusBooking>> getAllBusBookings() {
        return ResponseEntity.ok(busBookingService.getAllBookings());
    }
    
    @GetMapping("/GetBusBooking")
    public ResponseEntity<?> getBusBooking(@RequestParam Long bookingId) {
        return busBookingService.getBookingById(bookingId)
                .map(booking -> ResponseEntity.ok(booking))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/PostBusBooking")
    public ResponseEntity<BusBooking> postBusBooking(@RequestBody BusBooking booking) {
        return ResponseEntity.ok(busBookingService.createBooking(booking));
    }
    
    @DeleteMapping("/DeleteBusBooking")
    public ResponseEntity<?> deleteBusBooking(@RequestParam Long bookingId) {
        boolean deleted = busBookingService.deleteBooking(bookingId);
        
        if (deleted) {
            return ResponseEntity.ok("Booking deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
} 