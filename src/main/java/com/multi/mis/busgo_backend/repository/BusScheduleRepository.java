package com.multi.mis.busgo_backend.repository;

import com.multi.mis.busgo_backend.model.BusSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface BusScheduleRepository extends JpaRepository<BusSchedule, Long> {
    List<BusSchedule> findBySourceLocationLocationIdAndDestinationLocationLocationIdAndDepartureTimeGreaterThanEqual(
            Long sourceLocationId, Long destinationLocationId, Date departureTime);
    
    List<BusSchedule> findByCompanyId(Long companyId);
    
    List<BusSchedule> findByActive(boolean active);

    @Query("SELECT bs FROM BusSchedule bs " +
            "JOIN FETCH bs.company " +
            "JOIN FETCH bs.route " +
            "JOIN FETCH bs.sourceLocation " +
            "JOIN FETCH bs.destinationLocation " +
            "WHERE bs.sourceLocation.locationId = :sourceId " +
            "AND bs.destinationLocation.locationId = :destId " +
            "AND DATE(bs.departureTime) = DATE(:departureDate) " +
            "AND bs.availableSeats > 0 AND bs.active = true")
    List<BusSchedule> searchBus(
            @Param("sourceId") Long sourceId,
            @Param("destId") Long destId,
            @Param("departureDate") Date departureDate);

    @Query("SELECT bs FROM BusSchedule bs " +
            "JOIN FETCH bs.company " +
            "JOIN FETCH bs.route " +
            "JOIN FETCH bs.sourceLocation " +
            "JOIN FETCH bs.destinationLocation " +
            "WHERE bs.sourceLocation.city = :sourceCity " +
            "AND bs.destinationLocation.city = :destCity " +
            "AND DATE(bs.departureTime) = DATE(:departureDate) " +
            "AND bs.availableSeats > 0 AND bs.active = true")
    List<BusSchedule> searchBusByCity(
            @Param("sourceCity") String sourceCity,
            @Param("destCity") String destCity,
            @Param("departureDate") Date departureDate);

    List<BusSchedule> findBySourceLocationLocationIdAndDestinationLocationLocationIdAndDepartureTime(
            Long sourceId, Long destId, Date departureDate);

    List<BusSchedule> findBySourceLocationCityAndDestinationLocationCityAndDepartureTime(
            String sourceCity, String destCity, Date departureDate);

    List<BusSchedule> findByActiveAndDepartureTimeBefore(boolean active, Date departureTime);

    @Modifying
    @Query("UPDATE BusSchedule s SET s.active = :active WHERE s.scheduleId IN :ids")
    int updateActiveStatusByIdIn(@Param("ids") List<Long> ids, @Param("active") boolean active);
} 
