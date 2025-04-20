package com.multi.mis.busgo_backend.repository;

import com.multi.mis.busgo_backend.model.LocationAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocationAddressRepository extends JpaRepository<LocationAddress, Long> {
    Optional<LocationAddress> findByLocationLocationId(Long locationId);
} 