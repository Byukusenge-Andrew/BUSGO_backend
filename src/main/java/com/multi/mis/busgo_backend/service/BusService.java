package com.multi.mis.busgo_backend.service;

import com.multi.mis.busgo_backend.model.Bus;
import com.multi.mis.busgo_backend.repository.BusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class BusService {
    @Autowired
    private BusRepository busRepository;

    /**
     * Get all buses
     */
    public List<Bus> getAllBuses() {
        return busRepository.findAll();
    }

    /**
     * Get bus by ID
     */
    public Optional<Bus> getBusById(Long id) {
        return busRepository.findById(id);
    }

    /**
     * Get buses by company ID
     */
    public List<Bus> getBusesByCompany(String companyId) {
        return busRepository.findByCompanyId(companyId);
    }

    /**
     * Get buses by status
     */
    public List<Bus> getBusesByStatus(String status) {
        return busRepository.findByStatus(status);
    }

    /**
     * Get buses by company ID and status
     */
    public List<Bus> getBusesByCompanyAndStatus(String companyId, String status) {
        return busRepository.findByCompanyIdAndStatus(companyId, status);
    }

    /**
     * Save a bus
     */
    public Bus saveBus(Bus bus) {
        if (bus.getCreatedAt() == null) {
            bus.setCreatedAt(new Date());
        }
        return busRepository.save(bus);
    }

    /**
     * Update bus status
     */
    public Optional<Bus> updateBusStatus(Long id, String status) {
        return busRepository.findById(id)
                .map(bus -> {
                    bus.setStatus(status);
                    return busRepository.save(bus);
                });
    }
    /**
     * Get bus by type
     */

    public List<Bus> findbyBustype(String bustype) {
        return busRepository.findBusByBusType(bustype);

    }

    /**
     * Delete a bus
     */
    public void deleteBus(Long id) {
        busRepository.deleteById(id);
    }
}
