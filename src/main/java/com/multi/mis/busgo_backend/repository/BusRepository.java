package com.multi.mis.busgo_backend.repository;

import com.multi.mis.busgo_backend.model.Bus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusRepository extends JpaRepository<Bus, Long> {
    List<Bus> findByCompanyId(String companyId);
    List<Bus> findByStatus(String status);
    List<Bus> findByCompanyIdAndStatus(String companyId, String status);
    List<Bus> findBusByBusType(String busType);
}
