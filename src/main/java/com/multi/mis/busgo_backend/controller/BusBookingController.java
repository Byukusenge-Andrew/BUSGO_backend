
package com.multi.mis.busgo_backend.controller;

import com.multi.mis.busgo_backend.model.BusBooking;
import com.multi.mis.busgo_backend.model.BusSchedule;
import com.multi.mis.busgo_backend.model.User;
import com.multi.mis.busgo_backend.service.BusBookingService;
import com.multi.mis.busgo_backend.service.BusScheduleService;
import com.multi.mis.busgo_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/BusBooking")
@CrossOrigin(origins = "*")
public class BusBookingController {

    @Autowired
    private BusBookingService busBookingService;

    @Autowired
    private UserService userservice;

    @Autowired
    private BusScheduleService busScheduleservice;

    private static final Logger logger = Logger.getLogger(BusBookingController.class.getName());

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<BusBooking>> getBusBookingByCompanyId( @PathVariable Long companyId) {
//        List<BusBooking> bookings = busBookingService.getBookingByCompany(companyId);
//        return new ResponseEntity<>(bookings, HttpStatus.OK);
        return ResponseEntity.ok(busBookingService.getBookingByCompany(companyId));
    }

    /**
     * Get all bus bookings
     * @return List of all bus bookings
     */
    @GetMapping("/GetAllBusBookings")
    public ResponseEntity<List<BusBooking>> getAllBusBookings() {
        List<BusBooking> bookings = busBookingService.getAllBookings();
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    /**
     * Get a specific bus booking by ID
     * @param id The booking ID
     * @return The bus booking if found
     */
    @GetMapping("/GetBusBooking")
    public ResponseEntity<BusBooking> getBusBooking(@RequestParam Long id) {
        Optional<BusBooking> booking = Optional.ofNullable(busBookingService.getBookingById(id));
        return booking.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Create a new bus booking
     * @param requestMap The booking details
     * @return The created booking
     */
    @PostMapping("/PostBusBooking")
    public ResponseEntity<BusBooking> createBusBooking(@RequestBody Map<String, Object> requestMap) {
        try {
            logger.info("Creating new booking with request data: " + requestMap);

            // Extract user information
            Map<String, Object> userMap = (Map<String, Object>) requestMap.get("user");
            if (userMap == null || userMap.get("id") == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            Long userId = Long.valueOf(userMap.get("id").toString());
            User user = userservice.findById(userId);
            if (user == null) {
                logger.warning("User not found with ID: " + userId);
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            // Extract schedule information
            Map<String, Object> scheduleMap = (Map<String, Object>) requestMap.get("schedule");
            if (scheduleMap == null || scheduleMap.get("id") == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            Long scheduleId = Long.valueOf(scheduleMap.get("id").toString());
            Optional<BusSchedule> schedule = busScheduleservice.getScheduleById(scheduleId);
            if (schedule.isEmpty()) {
                logger.warning("Schedule not found with ID: " + scheduleId);
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            // Extract and parse booking date - FIXED
            Date bookingDate = new Date(); // Default to current date
            String bookingDateStr = (String) requestMap.get("bookingDate");
            if (bookingDateStr != null) {
                try {
                    // Parse ISO 8601 format date string
                    bookingDate = Date.from(Instant.parse(bookingDateStr));
                } catch (Exception e) {
                    logger.warning("Failed to parse date: " + bookingDateStr + ". Using current date instead. Error: " + e.getMessage());
                    // Keep the default current date
                }
            }

            // Extract other booking details
            String status = (String) requestMap.get("status");
            Integer numberOfSeats = requestMap.get("numberOfSeats") != null ?
                    Integer.valueOf(requestMap.get("numberOfSeats").toString()) : 0;

            Double totalFare = requestMap.get("totalFare") != null ?
                    Double.valueOf(requestMap.get("totalFare").toString()) : 0.0;

            String seatNumbers = (String) requestMap.get("seatNumbers");
            String paymentMethod = (String) requestMap.get("paymentMethod");
            String paymentStatus = (String) requestMap.get("paymentStatus");
            String transactionId = (String) requestMap.get("transactionId");

            // Create and populate the BusBooking object
            BusBooking booking = new BusBooking();
            booking.setUser(user);
            booking.setSchedule(schedule.orElse(null));
            booking.setBookingDate(bookingDate);
            booking.setStatus(status != null ? status : "PENDING");
            booking.setNumberOfSeats(numberOfSeats);
            booking.setTotalFare(totalFare);
            booking.setSeatNumbers(seatNumbers);
            booking.setPaymentMethod(paymentMethod != null ? paymentMethod : "PENDING");
            booking.setPaymentStatus(paymentStatus != null ? paymentStatus : "PENDING");
            booking.setTransactionId(transactionId);

            // Create the booking
            BusBooking createdBooking = busBookingService.createBooking(booking);
            logger.info("Successfully created booking with ID: " + createdBooking.getBookingId());

            return new ResponseEntity<>(createdBooking, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.severe("Error creating booking: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Delete a bus booking
     * @param id The booking ID to delete
     * @return Success/failure status
     */
    @DeleteMapping("/DeleteBusBooking")
    public ResponseEntity<Void> deleteBusBooking(@RequestParam Long id) {
        boolean deleted = busBookingService.deleteBooking(id);
        return deleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Get bookings by user ID
     * @param userId The user ID
     * @return List of bookings for the user
     */
    @GetMapping("/GetBookingsByUser")
    public ResponseEntity<List<BusBooking>> getBookingsByUser(@RequestParam Long userId) {
        List<BusBooking> bookings = busBookingService.getBookingsByUser(userId);
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    /**
     * Get bookings by schedule ID
     * @param scheduleId The schedule ID
     * @return List of bookings for the schedule
     */
    @GetMapping("/GetBookingsBySchedule")
    public ResponseEntity<List<BusBooking>> getBookingsBySchedule(@RequestParam Long scheduleId) {
        List<BusBooking> bookings = busBookingService.getBookingsBySchedule(scheduleId);
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    /**
     * Get bookings by date
     * @param date The booking date
     * @return List of bookings for the date
     */
    @GetMapping("/GetBookingsByDate")
    public ResponseEntity<List<BusBooking>> getBookingsByDate(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        List<BusBooking> bookings = busBookingService.getBookingsByDate(date);
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    /**
     * Get bookings by date range
     * @param startDate The start date
     * @param endDate The end date
     * @return List of bookings within the date range
     */
    @GetMapping("/GetBookingsByDateRange")
    public ResponseEntity<List<BusBooking>> getBookingsByDateRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        List<BusBooking> bookings = busBookingService.getBookingsByDateRange(startDate, endDate);
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }



    /**
     * Get active bookings for a user
     * @param userId The user ID
     * @param status The booking status
     * @return List of active bookings
     */
    @GetMapping("/GetActiveBookings")
    public ResponseEntity<List<BusBooking>> getActiveBookings(
            @RequestParam Long userId,
            @RequestParam String status) {
        List<BusBooking> bookings = busBookingService.getActiveBookings(userId, status);
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    /**
     * Cancel a booking
     * @param id The booking ID to cancel
     * @return Success/failure status
     */
    @PostMapping("/CancelBooking")
    public ResponseEntity<Void> cancelBooking(@RequestParam Long id) {
        boolean cancelled = busBookingService.cancelBooking(id);
        return cancelled ? new ResponseEntity<>(HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Get booked seats for a schedule
     * @param scheduleId The schedule ID
     * @return List of booked seat numbers
     */
    @GetMapping("/GetBookedSeats")
    public ResponseEntity<List<String>> getBookedSeats(@RequestParam Long scheduleId) {
        List<String> bookedSeats = busBookingService.getBookedSeatsBySchedule(scheduleId);
        return new ResponseEntity<>(bookedSeats, HttpStatus.OK);
    }

    /**
     * Update a booking
     * @param id The booking ID
     * @param booking The updated booking details
     * @return The updated booking
     */
    @PutMapping("/UpdateBooking")
    public ResponseEntity<BusBooking> updateBooking(
            @RequestParam Long id,
            @RequestBody BusBooking booking) {
        try {
            // First, get the existing booking to ensure we have a managed entity
            Optional<BusBooking> existingBookingOpt = Optional.ofNullable(busBookingService.getBookingById(id));
            if (existingBookingOpt.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            BusBooking existingBooking = existingBookingOpt.get();

            // Update only the fields that should be updated, keeping the existing relationships
            existingBooking.setStatus(booking.getStatus());
            existingBooking.setNumberOfSeats(booking.getNumberOfSeats());
            existingBooking.setTotalFare(booking.getTotalFare());
            existingBooking.setSeatNumbers(booking.getSeatNumbers());
            existingBooking.setPaymentMethod(booking.getPaymentMethod());
            existingBooking.setPaymentStatus(booking.getPaymentStatus());
            existingBooking.setTransactionId(booking.getTransactionId());

            // If booking date is provided, update it
            if (booking.getBookingDate() != null) {
                existingBooking.setBookingDate(booking.getBookingDate());
            }

            // We don't update the user or schedule relationships to avoid transient entity issues

            BusBooking updatedBooking = busBookingService.updateBooking(id, existingBooking);
            return new ResponseEntity<>(updatedBooking, HttpStatus.OK);
        } catch (Exception e) {
            logger.severe("Error updating booking: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get bookings by status
     * @param status The booking status
     * @return List of bookings with the specified status
     */
    @GetMapping("/GetBookingsByStatus")
    public ResponseEntity<List<BusBooking>> getBookingsByStatus(@RequestParam String status) {
        List<BusBooking> bookings = busBookingService.getBookingsByStatus(status);
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    /**
     * Get bookings by company
     * @param companyId The company ID
     * @return List of bookings for the company
     */
    @GetMapping("/GetBookingsByCompany")
    public ResponseEntity<List<BusBooking>> getBookingsByCompany(@RequestParam Long companyId) {
        List<BusBooking> bookings = busBookingService.getBookingsByCompany(companyId);
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    /**
     * Get bookings by route
     * @param routeId The route ID
     * @return List of bookings for the route
     */
    @GetMapping("/GetBookingsByRoute")
    public ResponseEntity<List<BusBooking>> getBookingsByRoute(@RequestParam Long routeId) {
        List<BusBooking> bookings = busBookingService.getBookingsByRoute(routeId);
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    /**
     * Check if a seat is available
     * @param scheduleId The schedule ID
     * @param seatNumber The seat number
     * @return True if the seat is available, false otherwise
     */
    @GetMapping("/IsSeatAvailable")
    public ResponseEntity<Boolean> isSeatAvailable(
            @RequestParam Long scheduleId,
            @RequestParam String seatNumber) {
        boolean available = busBookingService.isSeatAvailable(scheduleId, seatNumber);
        return new ResponseEntity<>(available, HttpStatus.OK);
    }

    /**
     * Check if multiple seats are available
     * @param scheduleId The schedule ID
     * @param seatNumbers The list of seat numbers
     * @return True if all seats are available, false otherwise
     */
    @PostMapping("/AreSeatsAvailable")
    public ResponseEntity<Boolean> areSeatsAvailable(
            @RequestParam Long scheduleId,
            @RequestBody List<String> seatNumbers) {
        boolean available = busBookingService.areSeatsAvailable(scheduleId, seatNumbers);
        return new ResponseEntity<>(available, HttpStatus.OK);
    }

    /**
     * Search bookings by term
     * @param searchTerm The search term
     * @return List of matching bookings
     */
    @GetMapping("/SearchBookings")
    public ResponseEntity<List<BusBooking>> searchBookings(@RequestParam String searchTerm) {
        List<BusBooking> bookings = busBookingService.searchBookings(searchTerm);
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    /**
     * Count bookings by user ID
     * @param userId The user ID
     * @return Count of bookings for the user
     */
    @GetMapping("/CountBookingsByUser")
    public ResponseEntity<Integer> countBookingsByUser(@RequestParam Long userId) {
        int count = busBookingService.CountBusBookingsByUserId(userId);
//        return new ResponseEntity<>(count, HttpStatus.OK);
        return ResponseEntity.ok(count);
    }
}
