package com.multi.mis.busgo_backend.repository;

import com.multi.mis.busgo_backend.dto.PopularRouteDTO;
import com.multi.mis.busgo_backend.model.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    List<Route> findByCompanyCompanyId(Long companyId);
    List<Route> findByActive(boolean active);
    List<Route> findByRouteNameContainingIgnoreCase(String routeName);
    List<Route> findByRouteCode(String routeCode);
    @Query("SELECT new com.multi.mis.busgo_backend.dto.PopularRouteDTO(r.routeName, COUNT(s)) " +
            "FROM Route r LEFT JOIN BusSchedule s ON r.routeId = s.route.routeId " +
            "GROUP BY r.routeId, r.routeName " +
            "ORDER BY COUNT(s) DESC, r.routeId DESC " +
            "LIMIT :limit")
    List<PopularRouteDTO> findPopularRoutes(@Param("limit") int limit);
}