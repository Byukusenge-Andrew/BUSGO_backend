package com.multi.mis.busgo_backend.repository;

import com.multi.mis.busgo_backend.model.BusBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusBookingRepository extends JpaRepository<BusBooking, Long> {
    List<BusBooking> findByUserId(Long id);

    List<BusBooking> findByScheduleScheduleId(Long scheduleId);

    @Query("SELECT b.seatNumbers FROM BusBooking b WHERE b.schedule.scheduleId = :scheduleId AND b.status != 'Cancelled'")
    List<String> getBookedSeatsByScheduleId(@Param("scheduleId") Long scheduleId);

    // This method is used by the getBookingByCompany method in the service
    @Query("SELECT b FROM BusBooking b WHERE b.schedule.company.companyId = :companyId")
    List<BusBooking> findByCompany_CompanyId(@Param("companyId") Long companyId);
}