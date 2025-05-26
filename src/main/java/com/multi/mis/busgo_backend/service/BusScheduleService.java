package com.multi.mis.busgo_backend.service;

import com.multi.mis.busgo_backend.model.BusSchedule;
import com.multi.mis.busgo_backend.repository.BusCompanyRepository;
import com.multi.mis.busgo_backend.repository.BusLocationRepository;
import com.multi.mis.busgo_backend.repository.BusScheduleRepository;
import com.multi.mis.busgo_backend.repository.RouteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BusScheduleService {

    private static final Logger logger = LoggerFactory.getLogger(BusScheduleService.class);
    
    private boolean initialized = false;

    @Autowired
    private BusScheduleRepository busScheduleRepository;

    @Autowired
    private BusCompanyRepository busCompanyRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private BusLocationRepository busLocationRepository;

    /**
     * Initialize the service and update past schedules when the application context is fully loaded.
     * This ensures transactions work properly during initialization.
     */
    @EventListener(ContextRefreshedEvent.class)
    @Transactional
    public void onApplicationEvent() {
        // Prevent duplicate initialization if the event fires multiple times
        if (!initialized) {
            logger.info("Initializing BusScheduleService and updating past schedules on startup...");
            int updatedCount = manuallyUpdatePastSchedulesStatus();
            logger.info("Startup initialization complete. Updated {} schedules.", updatedCount);
            initialized = true;
        }
    }

    public BusCompanyRepository getBusCompanyRepository() {
        return busCompanyRepository;
    }

    public RouteRepository getRouteRepository() {
        return routeRepository;
    }

    public BusLocationRepository getBusLocationRepository() {
        return busLocationRepository;
    }

    @Transactional
    public BusSchedule createSchedule(BusSchedule schedule) {
        logger.debug("Creating schedule: {}", schedule);

        // Validate required fields
        if (schedule.getCompany() == null || schedule.getCompany().getCompanyId() == null) {
            logger.error("Company or company ID is null");
            throw new IllegalArgumentException("Company ID is required");
        }
        if (schedule.getRoute() == null || schedule.getRoute().getRouteId() == null) {
            logger.error("Route or route ID is null");
            throw new IllegalArgumentException("Route ID is required");
        }
        if (schedule.getSourceLocation() == null || schedule.getSourceLocation().getLocationId() == null) {
            logger.error("Source location or location ID is null");
            throw new IllegalArgumentException("Source location ID is required");
        }
        if (schedule.getDestinationLocation() == null || schedule.getDestinationLocation().getLocationId() == null) {
            logger.error("Destination location or location ID is null");
            throw new IllegalArgumentException("Destination location ID is required");
        }
        if (schedule.getDepartureTime() == null) {
            logger.error("Departure time is required");
            throw new IllegalArgumentException("Departure time is required");
        }
        if (schedule.getArrivalTime() == null) {
            logger.error("Arrival time is required");
            throw new IllegalArgumentException("Arrival time is required");
        }
        if (!schedule.getArrivalTime().after(schedule.getDepartureTime())) {
            logger.error("Arrival time must be after departure time");
            throw new IllegalArgumentException("Arrival time must be after departure time");
        }
        if (schedule.getFare() == null || schedule.getFare() < 0) {
            logger.error("Invalid fare");
            throw new IllegalArgumentException("Fare is required and must be non-negative");
        }
        if (schedule.getTotalSeats() == null || schedule.getTotalSeats() <= 0) {
            logger.error("Invalid total seats");
            throw new IllegalArgumentException("Total seats must be positive");
        }
        if (schedule.getAvailableSeats() == null || schedule.getAvailableSeats() < 0 ||
                schedule.getAvailableSeats() > schedule.getTotalSeats()) {
            logger.error("Invalid available seats");
            throw new IllegalArgumentException("Available seats must be non-negative and not exceed total seats");
        }
        if (schedule.getBusNumber() == null || schedule.getBusNumber().trim().isEmpty()) {
            logger.error("Bus number is required");
            throw new IllegalArgumentException("Bus number is required");
        }

        // Set active by default if not specified
        if (!schedule.isActive()) {
            schedule.setActive(true);
        }

        logger.debug("Saving schedule: {}", schedule);
        return busScheduleRepository.save(schedule);
    }

    public List<BusSchedule> getAllSchedules() {
        return busScheduleRepository.findAll();
    }

    /**
     * Gets all active schedules with departure time not in the past
     * @return List of valid schedules
     */
    public List<BusSchedule> getAllActiveSchedules() {
        List<BusSchedule> allSchedules = busScheduleRepository.findAll();
        Date currentDate = new Date();
        return allSchedules.stream()
                .filter(schedule -> schedule.isActive() && schedule.getDepartureTime().after(currentDate))
                .collect(Collectors.toList());
    }

    public Optional<BusSchedule> getScheduleById(Long scheduleId) {
        return busScheduleRepository.findById(scheduleId);
    }

    @Transactional
    public Optional<BusSchedule> updateSchedule(Long scheduleId, BusSchedule updatedSchedule) {
        return busScheduleRepository.findById(scheduleId)
                .map(existingSchedule -> {
                    if (updatedSchedule.getCompany() != null && updatedSchedule.getCompany().getCompanyId() != null) {
                        existingSchedule.setCompany(updatedSchedule.getCompany());
                    }
                    if (updatedSchedule.getRoute() != null && updatedSchedule.getRoute().getRouteId() != null) {
                        existingSchedule.setRoute(updatedSchedule.getRoute());
                    }
                    if (updatedSchedule.getSourceLocation() != null && updatedSchedule.getSourceLocation().getLocationId() != null) {
                        existingSchedule.setSourceLocation(updatedSchedule.getSourceLocation());
                    }
                    if (updatedSchedule.getDestinationLocation() != null && updatedSchedule.getDestinationLocation().getLocationId() != null) {
                        existingSchedule.setDestinationLocation(updatedSchedule.getDestinationLocation());
                    }
                    if (updatedSchedule.getDepartureTime() != null) {
                        existingSchedule.setDepartureTime(updatedSchedule.getDepartureTime());
                    }
                    if (updatedSchedule.getArrivalTime() != null) {
                        existingSchedule.setArrivalTime(updatedSchedule.getArrivalTime());
                    }
                    if (updatedSchedule.getArrivalTime() != null && updatedSchedule.getDepartureTime() != null &&
                            !updatedSchedule.getArrivalTime().after(updatedSchedule.getDepartureTime())) {
                        throw new IllegalArgumentException("Arrival time must be after departure time");
                    }
                    if (updatedSchedule.getFare() != null) {
                        existingSchedule.setFare(updatedSchedule.getFare());
                    }
                    if (updatedSchedule.getBusType() != null) {
                        existingSchedule.setBusType(updatedSchedule.getBusType());
                    }
                    if (updatedSchedule.getTotalSeats() != null) {
                        existingSchedule.setTotalSeats(updatedSchedule.getTotalSeats());
                    }
                    if (updatedSchedule.getAvailableSeats() != null) {
                        existingSchedule.setAvailableSeats(updatedSchedule.getAvailableSeats());
                    }
                    if (updatedSchedule.getBusNumber() != null) {
                        existingSchedule.setBusNumber(updatedSchedule.getBusNumber());
                    }
                    existingSchedule.setActive(updatedSchedule.isActive());
                    return busScheduleRepository.save(existingSchedule);
                });
    }

    public boolean deleteSchedule(Long scheduleId) {
        if (busScheduleRepository.existsById(scheduleId)) {
            busScheduleRepository.deleteById(scheduleId);
            return true;
        }
        return false;
    }

    public List<BusSchedule> searchBus(Long sourceId, Long destId, Date departureDate) {
        // Get the current date and time
        Date currentDate = new Date();
        
        // Get schedules from repository
        List<BusSchedule> schedules = busScheduleRepository.findBySourceLocationLocationIdAndDestinationLocationLocationIdAndDepartureTime(
                sourceId, destId, departureDate);
        
        // Filter out schedules with departure time in the past
        return schedules.stream()
                .filter(schedule -> schedule.isActive() && schedule.getDepartureTime().after(currentDate))
                .collect(Collectors.toList());
    }

    public List<BusSchedule> searchBusByCity(String sourceCity, String destCity, Date departureDate) {
        // Get the current date and time
        Date currentDate = new Date();
        
        // Get schedules from repository
        List<BusSchedule> schedules = busScheduleRepository.findBySourceLocationCityAndDestinationLocationCityAndDepartureTime(
                sourceCity, destCity, departureDate);
        
        // Filter out schedules with departure time in the past
        return schedules.stream()
                .filter(schedule -> schedule.isActive() && schedule.getDepartureTime().after(currentDate))
                .collect(Collectors.toList());
    }

    public List<BusSchedule> getBusScheduleByCompany(Long companyId) {
        return busScheduleRepository.findByCompanyId(companyId);
    }

    /**
     * Automatically updates the active status of schedules whose departure time has passed.
     * This task runs daily at a fixed time (e.g., 3:00 AM).
     */
    @Scheduled(cron = "0 0 3 * * ?") // Runs every day at 3:00 AM
    @Transactional
    public void updatePastSchedulesStatus() {
        logger.info("Starting scheduled task to update past schedules status.");
        Date now = new Date();
        
        // Find all active schedules with departure time in the past
        List<BusSchedule> pastSchedules = busScheduleRepository.findByActiveAndDepartureTimeBefore(true, now);

        if (pastSchedules.isEmpty()) {
            logger.info("No past active schedules found.");
            return;
        }

        List<Long> scheduleIdsToUpdate = pastSchedules.stream()
                .map(BusSchedule::getScheduleId)
                .collect(Collectors.toList());

        // Set active to false for past schedules
        int updatedCount = busScheduleRepository.updateActiveStatusByIdIn(scheduleIdsToUpdate, false);

        logger.info("Updated {} past schedules to inactive (active=false).", updatedCount);
    }
    
    /**
     * Manual method to update status of past schedules.
     * Can be called from a controller or other service methods.
     */
    @Transactional
    public int manuallyUpdatePastSchedulesStatus() {
        logger.info("Manually updating past schedules status.");
        Date now = new Date();
        
        // Find all active schedules with departure time in the past
        List<BusSchedule> pastSchedules = busScheduleRepository.findByActiveAndDepartureTimeBefore(true, now);

        if (pastSchedules.isEmpty()) {
            logger.info("No past active schedules found.");
            return 0;
        }

        List<Long> scheduleIdsToUpdate = pastSchedules.stream()
                .map(BusSchedule::getScheduleId)
                .collect(Collectors.toList());

        // Set active to false for past schedules
        int updatedCount = busScheduleRepository.updateActiveStatusByIdIn(scheduleIdsToUpdate, false);

        logger.info("Manually updated {} past schedules to inactive (active=false).", updatedCount);
        return updatedCount;
    }
}
