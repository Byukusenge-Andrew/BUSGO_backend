package com.multi.mis.busgo_backend.service;

import com.multi.mis.busgo_backend.model.BusCompany;
import com.multi.mis.busgo_backend.model.BusLocation;
import com.multi.mis.busgo_backend.model.BusSchedule;
import com.multi.mis.busgo_backend.model.Route;
import com.multi.mis.busgo_backend.repository.BusCompanyRepository;
import com.multi.mis.busgo_backend.repository.BusLocationRepository;
import com.multi.mis.busgo_backend.repository.BusScheduleRepository;
import com.multi.mis.busgo_backend.repository.RouteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class BusScheduleService {

    private static final Logger logger = LoggerFactory.getLogger(BusScheduleService.class);

    @Autowired
    private BusScheduleRepository busScheduleRepository;

    @Autowired
    private BusCompanyRepository busCompanyRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private BusLocationRepository busLocationRepository;

    // Expose repositories for BusScheduleController
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
        return busScheduleRepository.findBySourceLocationLocationIdAndDestinationLocationLocationIdAndDepartureTime(
                sourceId, destId, departureDate);
    }

    public List<BusSchedule> searchBusByCity(String sourceCity, String destCity, Date departureDate) {
        return busScheduleRepository.findBySourceLocationCityAndDestinationLocationCityAndDepartureTime(
                sourceCity, destCity, departureDate);
    }
}