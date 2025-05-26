package com.multi.mis.busgo_backend.service;

import com.multi.mis.busgo_backend.model.BusBooking;
import com.multi.mis.busgo_backend.model.BusSchedule;
import com.multi.mis.busgo_backend.repository.BusBookingRepository;
import com.multi.mis.busgo_backend.repository.BusScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.multi.mis.busgo_backend.model.Ticket;
import com.multi.mis.busgo_backend.service.TicketService;

@Service
public class BusBookingService {
    
    @Autowired
    private BusBookingRepository busBookingRepository;


    
    @Autowired
    private BusScheduleRepository busScheduleRepository;
    
    @Autowired
    private TicketService ticketService;
    
    public List<BusBooking> getAllBookings() {
        return busBookingRepository.findAll();
    }
    
    public Page<BusBooking> getAllBookingsPaginated(Pageable pageable) {
        return busBookingRepository.findAll(pageable);
    }

    @Transactional
    public BusBooking getBookingById(Long bookingId) {
        BusBooking booking = busBookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + bookingId));
        if (booking.getSchedule() == null) {
            throw new RuntimeException("Booking ID: " + bookingId + " has no associated schedule.");
        }
        return booking;
    }

    @Transactional
    public void confirmBookingAfterPayment(Long bookingId) {
        BusBooking booking = getBookingById(bookingId);
        booking.setStatus("CONFIRMED");
        booking.setPaymentStatus("COMPLETED");
        BusBooking confirmedBooking = busBookingRepository.save(booking);

        createTicketFromBooking(confirmedBooking);
    }

    public int CountBusBookingsByUserId(Long userId) {
        List<BusBooking> userBookings = getBookingsByUser(userId);

        // Return the count of bookings
        return userBookings.size();
    }

    public List<BusBooking> getBookingByCompany(Long companyId) {
        return busBookingRepository.findByCompany_CompanyId(companyId);
    }





    public List<BusBooking> getBookingsByUser(Long userId) {
        return busBookingRepository.findAll().stream()
            .filter(booking -> booking.getUser().getId().equals(userId))
            .collect(Collectors.toList());
    }
    
    public Page<BusBooking> getBookingsByUserPaginated(Long userId, Pageable pageable) {
        return busBookingRepository.findByUserId(userId, pageable);
    }
    
    public List<BusBooking> getBookingsBySchedule(Long scheduleId) {
        return busBookingRepository.findAll().stream()
            .filter(booking -> booking.getSchedule().getId().equals(scheduleId))
            .collect(Collectors.toList());
    }
    public List<BusBooking> getBookingsByDate(Date date) {
        return busBookingRepository.findAll().stream()
            .filter(booking -> booking.getBookingDate().equals(date))
            .collect(Collectors.toList());
    }


    public List<BusBooking> getBookingsByDateRange(Date startDate, Date endDate) {
        return busBookingRepository.findAll().stream()
            .filter(booking -> booking.getBookingDate().after(startDate) && booking.getBookingDate().before(endDate))
            .collect(Collectors.toList());
    }
    public List<BusBooking> getActiveBookings(Long userId,String status) {
        return busBookingRepository.findAll().stream()
            .filter(booking -> booking.getUser().getId().equals(userId) )
            .collect(Collectors.toList());
    }
    
    @Transactional
    public BusBooking createBooking(BusBooking booking) {
        // Set booking date
        booking.setBookingDate(new Date());
        
        // Update available seats in the schedule
        BusSchedule schedule = booking.getSchedule();
        if (schedule != null && schedule.getAvailableSeats() >= booking.getNumberOfSeats()) {
            schedule.setAvailableSeats(schedule.getAvailableSeats() - booking.getNumberOfSeats());
            busScheduleRepository.save(schedule);
            return busBookingRepository.save(booking);
        } else {
            throw new RuntimeException("Not enough seats available for booking");
        }
    }
    
    @Transactional
    public boolean cancelBooking(Long id) {
        Optional<BusBooking> bookingOpt = busBookingRepository.findById(id);
        
        if (bookingOpt.isPresent()) {
            BusBooking booking = bookingOpt.get();
            
            // Update status to Cancelled
            booking.setStatus("Cancelled");
            busBookingRepository.save(booking);
            
            // Return seats to available pool
            BusSchedule schedule = booking.getSchedule();
            schedule.setAvailableSeats(schedule.getAvailableSeats() + booking.getNumberOfSeats());
            busScheduleRepository.save(schedule);
            
            return true;
        }
        
        return false;
    }
    
    public boolean deleteBooking(Long id) {
        if (busBookingRepository.existsById(id)) {
            busBookingRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    public List<String> getBookedSeatsBySchedule(Long scheduleId) {
        return getBookingsBySchedule(scheduleId).stream()
            .filter(booking -> !booking.getStatus().equalsIgnoreCase("CANCELLED"))
            .flatMap(booking -> {
                String[] seats = booking.getSeatNumbers().split(",");
                return java.util.Arrays.stream(seats).map(String::trim);
            })
            .collect(Collectors.toList());
    }

    /**
     * Updates an existing booking with the provided data
     * @param id The ID of the booking to update
     * @param booking The booking data with updated fields
     * @return The updated booking
     */
    @Transactional
    public BusBooking updateBooking(Long id, BusBooking booking) {
        // Ensure the ID is set correctly
        booking.setId(id);

        // Get the existing booking to ensure we're working with a managed entity
        Optional<BusBooking> existingBookingOpt = busBookingRepository.findById(id);
        if (existingBookingOpt.isEmpty()) {
            return null;
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

        // Save the updated booking
        return busBookingRepository.save(existingBooking);
    }

    
    /**
     * Updates the status of a specific booking.
     * @param bookingId The ID of the booking to update.
     * @param status The new status (e.g., "CONFIRMED", "CANCELLED").
     * @return The updated booking.
     * @throws RuntimeException if the booking is not found.
     */
    @Transactional
    public BusBooking updateBookingStatus(Long bookingId, String status) {
        BusBooking booking = busBookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + bookingId));
        booking.setStatus(status);
        return busBookingRepository.save(booking);
    }
    
    public List<BusBooking> getBookingsByStatus(String status) {
        return busBookingRepository.findAll().stream()
            .filter(booking -> booking.getStatus().equalsIgnoreCase(status))
            .collect(Collectors.toList());
    }
    
    public List<BusBooking> getBookingsByCompany(Long companyId) {
        return busBookingRepository.findAll().stream()
            .filter(booking -> booking.getSchedule().getRoute().getCompany().getId().equals(companyId))
            .collect(Collectors.toList());
    }
    
    public List<BusBooking> getBookingsByRoute(Long routeId) {
        return busBookingRepository.findAll().stream()
            .filter(booking -> booking.getSchedule().getRoute().getId().equals(routeId))
            .collect(Collectors.toList());
    }
    
    public boolean isSeatAvailable(Long scheduleId, String seatNumber) {
        List<String> bookedSeats = getBookedSeatsBySchedule(scheduleId);
        return !bookedSeats.contains(seatNumber);
    }
    
    public boolean areSeatsAvailable(Long scheduleId, List<String> seatNumbers) {
        List<String> bookedSeats = getBookedSeatsBySchedule(scheduleId);
        return seatNumbers.stream().noneMatch(bookedSeats::contains);
    }
    
    public List<BusBooking> searchBookings(String searchTerm) {
        String searchLower = searchTerm.toLowerCase();
        return busBookingRepository.findAll().stream()
            .filter(booking -> 
                booking.getId().toString().contains(searchLower) ||
                booking.getUser().getFirstName().toLowerCase().contains(searchLower) ||
                booking.getUser().getLastName().toLowerCase().contains(searchLower) ||
                booking.getSchedule().getRoute().getName().toLowerCase().contains(searchLower))
            .collect(Collectors.toList());
    }

    /**
     * Creates a Ticket entity from a confirmed BusBooking.
     * @param booking The confirmed BusBooking object.
     */
    @Transactional
    public void createTicketFromBooking(BusBooking booking) {
        Ticket ticket = new Ticket();
        ticket.setBookingId(booking.getBookingId());
        // Split the comma-separated seat numbers string into a list
        if (booking.getSeatNumbers() != null && !booking.getSeatNumbers().isEmpty()) {
            ticket.setSeatNumbers(java.util.Arrays.asList(booking.getSeatNumbers().split(",")));
        } else {
            ticket.setSeatNumbers(java.util.Collections.emptyList());
        }
        ticket.setPrice(booking.getTotalFare());
        ticket.setStatus("CONFIRMED");
        ticket.setPaymentStatus(booking.getPaymentStatus());

        if (booking.getUser() != null) {
            ticket.setPassengerName(booking.getUser().getFirstName() + " " + booking.getUser().getLastName());
            ticket.setPassengerEmail(booking.getUser().getEmail());
            ticket.setPassengerPhone(booking.getUser().getPhoneNumber());
        }

        if (booking.getSchedule() != null) {
            BusSchedule schedule = booking.getSchedule();
            if (schedule.getRoute() != null) {
                ticket.setRouteName(schedule.getRoute().getName());
                ticket.setOrigin(schedule.getRoute().getOrigin());
                ticket.setDestination(schedule.getRoute().getDestination());
            }
            ticket.setDepartureDate(schedule.getDepartureTime());
            ticket.setDepartureTime(schedule.getDepartureTime() != null ? schedule.getDepartureTime().toString() : null);

            ticket.setBusRegistration(schedule.getBusNumber());
        }

        ticket.setCreatedAt(new Date());

        ticketService.saveTicket(ticket);
    }

} 
