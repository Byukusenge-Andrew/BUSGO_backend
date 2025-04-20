package com.multi.mis.busgo_backend.service;

import com.multi.mis.busgo_backend.model.BusSchedule;
import com.multi.mis.busgo_backend.repository.BusScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class BusScheduleService {
    
    @Autowired
    private BusScheduleRepository busScheduleRepository;
    
    public List<BusSchedule> getAllSchedules() {
        return busScheduleRepository.findAll();
    }
    
    public Optional<BusSchedule> getScheduleById(Long id) {
        return busScheduleRepository.findById(id);
    }
    
    public BusSchedule createSchedule(BusSchedule schedule) {
        schedule.setActive(true);
        return busScheduleRepository.save(schedule);
    }
    
    public Optional<BusSchedule> updateSchedule(Long id, BusSchedule updatedSchedule) {
        return busScheduleRepository.findById(id)
                .map(existingSchedule -> {
                    if (updatedSchedule.getCompany() != null) {
                        existingSchedule.setCompany(updatedSchedule.getCompany());
                    }
                    if (updatedSchedule.getSourceLocation() != null) {
                        existingSchedule.setSourceLocation(updatedSchedule.getSourceLocation());
                    }
                    if (updatedSchedule.getDestinationLocation() != null) {
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
                    return busScheduleRepository.save(existingSchedule);
                });
    }
    
    public boolean deleteSchedule(Long id) {
        if (busScheduleRepository.existsById(id)) {
            busScheduleRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    public List<BusSchedule> searchBus(Long sourceId, Long destId, Date departureDate) {
        return busScheduleRepository.searchBus(sourceId, destId, departureDate);
    }
    
    public List<BusSchedule> searchBusByCity(String sourceCity, String destCity, Date departureDate) {
        return busScheduleRepository.searchBusByCity(sourceCity, destCity, departureDate);
    }
    
    public List<BusSchedule> getSchedulesByCompany(Long companyId) {
        return busScheduleRepository.findByCompanyCompanyId(companyId);
    }
    
    public List<BusSchedule> getActiveSchedules() {
        return busScheduleRepository.findByActive(true);
    }
} 