package com.multi.mis.busgo_backend.repository;

import com.multi.mis.busgo_backend.model.BusLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusLocationRepository extends JpaRepository<BusLocation, Long> {
    List<BusLocation> findByCityContainingIgnoreCase(String city);
    List<BusLocation> findByStateContainingIgnoreCase(String state);
    List<BusLocation> findByLocationNameContainingIgnoreCase(String locationName);
} 