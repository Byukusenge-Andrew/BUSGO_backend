package com.multi.mis.busgo_backend.repository;

import com.multi.mis.busgo_backend.model.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    List<Route> findByCompanyCompanyId(Long companyId);
    List<Route> findByActive(boolean active);
    List<Route> findByRouteNameContainingIgnoreCase(String routeName);
    List<Route> findByRouteCode(String routeCode);
} 