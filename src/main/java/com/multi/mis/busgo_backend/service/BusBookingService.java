package com.multi.mis.busgo_backend.service;

import com.multi.mis.busgo_backend.model.BusBooking;
import com.multi.mis.busgo_backend.model.BusSchedule;
import com.multi.mis.busgo_backend.repository.BusBookingRepository;
import com.multi.mis.busgo_backend.repository.BusScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BusBookingService {
    
    @Autowired
    private BusBookingRepository busBookingRepository;
    
    @Autowired
    private BusScheduleRepository busScheduleRepository;
    
    public List<BusBooking> getAllBookings() {
        return busBookingRepository.findAll();
    }
    
    public Optional<BusBooking> getBookingById(Long id) {
        return busBookingRepository.findById(id);
    }


    public int CountBusBookingsByUserId(Long userId) {
        List<BusBooking> userBookings = getBookingsByUser(userId);

        // Return the count of bookings
        return userBookings.size();
    }


    public List<BusBooking> getBookingsByUser(Long userId) {
        return busBookingRepository.findAll().stream()
            .filter(booking -> booking.getUser().getId().equals(userId))
            .collect(Collectors.toList());
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
            .filter(booking -> booking.getUser().getId().equals(userId) && booking.getStatus().equalsIgnoreCase(status))
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
    
    public BusBooking updateBooking(Long id, BusBooking booking) {
        booking.setId(id);
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
} 