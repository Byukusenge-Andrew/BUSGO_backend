package com.multi.mis.busgo_backend.repository;

import com.multi.mis.busgo_backend.model.RouteStop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteStopRepository extends JpaRepository<RouteStop, Long> {
    List<RouteStop> findByRouteRouteId(Long routeId);
    List<RouteStop> findByLocationLocationId(Long locationId);
    List<RouteStop> findByRouteRouteIdOrderBySequenceNumberAsc(Long routeId);
    List<RouteStop> findByRouteRouteIdAndIsPickupPointTrue(Long routeId);
    List<RouteStop> findByRouteRouteIdAndIsDropPointTrue(Long routeId);
} 